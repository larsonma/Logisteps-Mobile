package com.example.mobilephone.Services;

import com.example.mobilephone.Models.Step;
import com.example.mobilephone.Models.StepSummary;
import com.example.mobilephone.Models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface LogistepsService {
    @GET("user/{user}/")
    Call<User> getUser(@Path("user") String username, @Header("Authorization") String credentials);

    @POST("user/")
    Call<User> postUser(@Body User user);

    @GET("steps/summary/")
    Call<StepSummary> getStepSummary(@Header("Authorization") String credentials);

    @POST("steps/")
    Call<List<Step>> postSteps(@Header("Authorization") String credentials, @Body List<Step> steps);
}
