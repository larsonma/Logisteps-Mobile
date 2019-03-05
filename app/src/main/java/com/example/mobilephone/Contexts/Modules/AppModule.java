package com.example.mobilephone.Contexts.Modules;

import android.app.Application;

import com.example.mobilephone.Databases.StepSummaryDao;
import com.example.mobilephone.Databases.StepSummaryDatabase;
import com.example.mobilephone.Services.LogistepsService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

import androidx.room.Room;
import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module(includes = ViewModelModule.class)
public class AppModule {
    //Databases
    @Provides
    @Singleton
    StepSummaryDatabase provideDatabase(Application application) {
        return Room.databaseBuilder(application, StepSummaryDatabase.class, "StepSummary.db")
                .build();
    }

    @Provides
    @Singleton
    StepSummaryDao provideStepSummaryDao(StepSummaryDatabase database) {
        return database.stepSummaryDao();
    }

    //Repositories
    @Provides
    Executor provideExecutor() {
        return Executors.newSingleThreadExecutor();
    }

    //API
    private static String BASE_URL = "http://127.0.0.1:8000/api/";

    @Provides
    Gson provideGson() {
        return new GsonBuilder().create();
    }

    @Provides
    Retrofit provideRetrofit(Gson gson) {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(BASE_URL)
                .build();
        return retrofit;
    }

    @Provides
    @Singleton
    LogistepsService provideApiWebservice(Retrofit restAdapter) {
        return restAdapter.create(LogistepsService.class);
    }
}
