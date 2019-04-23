package com.example.mobilephone.Repositories;

import android.util.Log;

import com.example.mobilephone.Models.Step;
import com.example.mobilephone.Models.User;
import com.example.mobilephone.Services.LogistepsService;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.Credentials;
import retrofit2.Response;

import static androidx.constraintlayout.widget.Constraints.TAG;

@Singleton
public class StepRepository {
    private final LogistepsService webservice;
    private final Executor executor;

    @Inject
    public StepRepository(LogistepsService webservice, Executor executor) {
        this.webservice = webservice;
        this.executor = executor;
    }

    public void postSteps(List<Step> steps, User user) {
        executor.execute(() -> {
            try {
                Response<List<Step>> response = webservice
                        .postSteps(Credentials.basic(
                            user.getBaseUser().getUsername(),
                            user.getBaseUser().getPassword()),
                            steps)
                        .execute();
            } catch (IOException e) {
                Log.e(TAG, e.toString());
            }
        });
    }
}

