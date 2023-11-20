package com.damhoe.scoresheetskat;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {

   private final Application mApplication;

   @Singleton
   protected ApplicationModule(Application app) {
      mApplication = app;
   }

   @Provides
   @ApplicationContext
   Context provideContext() {
      return mApplication;
   }

   @Provides
   @DatabaseInfo
   String provideDatabaseName() {
      return "score-sheet-skat.db";
   }

   @Provides
   @DatabaseInfo
   Integer provideDatabaseVersion() {
      return 3;
   }
}
