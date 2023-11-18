package com.damhoe.scoresheetskat.player.adapter.out;

import android.content.res.Resources;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.damhoe.scoresheetskat.base.Result;
import com.damhoe.scoresheetskat.player.application.ports.out.CreatePlayerPort;
import com.damhoe.scoresheetskat.player.application.ports.out.GetPlayerPort;
import com.damhoe.scoresheetskat.player.application.ports.out.UpdatePlayerPort;
import com.damhoe.scoresheetskat.player.domain.Player;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PlayerRepository implements CreatePlayerPort, GetPlayerPort, UpdatePlayerPort {

   private final PlayerPersistenceAdapter mPersistenceAdapter;

   private final MutableLiveData<List<Player>> players = new MutableLiveData<>();

   @Inject
   PlayerRepository(PlayerPersistenceAdapter persistenceAdapter) {
      mPersistenceAdapter = persistenceAdapter;

      // Load data
      players.setValue(
              persistenceAdapter.loadAllPlayers().stream()
                      .map(playerDTO -> {
                         int gameCount = mPersistenceAdapter.getGameCount(playerDTO.getId());
                         return playerDTO.asPlayer(gameCount);
                      })
                      .collect(Collectors.toList())
      );
   }

   @Override
   public Player createPlayer(String name) {
      Player player = new Player(name);
      PlayerDTO playerDTO = PlayerDTO.fromPlayer(player);
      long id = mPersistenceAdapter.insertPlayer(playerDTO);
      player.setID(id);

      // Update player list
      refreshPlayersFromRepository();

      return player;
   }

   @Override
   public Player deletePlayer(long id) {
      Result<PlayerDTO> result = mPersistenceAdapter.deletePlayer(id);

      if (result.isFailure()) {
         throw new Resources.NotFoundException(result.getMessage());
      }

      mPersistenceAdapter.deletePlayerMatches(id);

      // Update player list
      refreshPlayersFromRepository();

      int gameCount = mPersistenceAdapter.getGameCount(id);
      return result.getValue().asPlayer(gameCount);
   }

   @Override
   public Player getPlayer(long id) {
      Result<PlayerDTO> getPlayerResult = mPersistenceAdapter.getPlayer(id);
      if (getPlayerResult.isFailure()) {
         throw new Resources.NotFoundException(getPlayerResult.getMessage());
      }
      int gameCount = mPersistenceAdapter.getGameCount(id);
      return getPlayerResult.getValue().asPlayer(gameCount);
   }

   @Override
   public LiveData<List<Player>> getPlayersLiveData() {
      return players;
   }

   @Override
   public List<Player> getPlayers() {
      return players.getValue();
   }

   @Override
   public void refreshPlayersFromRepository() {
      players.postValue(
              mPersistenceAdapter.loadAllPlayers().stream()
                      .map(playerDTO -> {
                         int gameCount = mPersistenceAdapter.getGameCount(playerDTO.getId());
                         return playerDTO.asPlayer(gameCount);
                      })
                      .collect(Collectors.toList())
      );
   }

   @Override
   public Player updateName(long id, String newName) {
      Player player = getPlayer(id);
      player.setName(newName);
      // Convert to database model
      PlayerDTO dto = PlayerDTO.fromPlayer(player);
      // Persist changes
      boolean isSuccess = mPersistenceAdapter.updatePlayer(dto) == 1;

      // Update player list
      refreshPlayersFromRepository();

      return player;
   }
}
