package com.damhoe.scoresheetskat.score.application

import com.damhoe.scoresheetskat.score.application.ports.`in`.CreateScoreUseCase
import com.damhoe.scoresheetskat.score.application.ports.`in`.GetScoreUseCase
import com.damhoe.scoresheetskat.score.application.ports.out.CreateScorePort
import com.damhoe.scoresheetskat.score.application.ports.out.GetScoresPort
import com.damhoe.scoresheetskat.score.domain.SkatScore
import com.damhoe.scoresheetskat.score.domain.SkatScoreCommand
import javax.inject.Inject

class ScoreService @Inject constructor(
    private val getScoresPort: GetScoresPort,
    private val createScorePort: CreateScorePort
) : CreateScoreUseCase, GetScoreUseCase {

    override fun createScore(command: SkatScoreCommand): Result<SkatScore> =
        SkatScore(command).let { createScorePort.saveScore(it) }

    override fun getScores(gameId: Long): Result<List<SkatScore>> = getScoresPort.getScores(gameId)

    override fun getScore(id: Long): Result<SkatScore> = getScoresPort.getScore(id)

    override fun updateScore(id: Long, command: SkatScoreCommand): Result<Unit> =
        SkatScore(command).let {
            it.id = id
            createScorePort.updateScore(it)
        }

    override fun deleteScore(id: Long): Result<SkatScore> = createScorePort.deleteScore(id)

    override fun deleteScoresForGame(gameId: Long): Result<Int> =
        createScorePort.deleteScoresForGame(gameId)
}