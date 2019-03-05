package com.example.mobilephone.Repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


import com.example.mobilephone.Databases.StepSummaryDao;
import com.example.mobilephone.Models.StepSummary;
import com.example.mobilephone.Models.User;
import com.example.mobilephone.Services.LogistepsService;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.Credentials;
import retrofit2.Response;

@Singleton
public class StepSummaryRepository {
    private static int FRESH_TIMEOUT_IN_MINUTES = 1;

    private final LogistepsService webservice;
    private final StepSummaryDao stepSummaryDao;
    private final Executor executor;

    @Inject
    public StepSummaryRepository(LogistepsService webservice, StepSummaryDao stepSummaryDao, Executor executor) {
        this.webservice = webservice;
        this.stepSummaryDao = stepSummaryDao;
        this.executor = executor;
    }

    public LiveData<StepSummary> getStepSummary(User user, int summaryId) {
        refreshStepSummary(user, summaryId);
        // Returns a LiveData object directly from the database.
        return stepSummaryDao.load(summaryId);
    }

    private void refreshStepSummary(final User user, final int summaryId) {
        // Runs in a background thread.
        executor.execute(() -> {
            StepSummary stepSummaryExists = stepSummaryDao.hasStepSummary(summaryId, getMaxRefreshTime(new Date()));
            if (stepSummaryExists == null) {
                //Refreshes the data.
                try {
                    Response<StepSummary> response = webservice.getStepSummary(Credentials.basic(user.getUsername(), user.getPassword())).execute();
                    stepSummaryDao.save(response.body());
                } catch (IOException e) {
                    //TODO: Check for errors
                }
            }
        });
    }

    private Date getMaxRefreshTime(Date currentDate){
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        cal.add(Calendar.MINUTE, -FRESH_TIMEOUT_IN_MINUTES);
        return cal.getTime();
    }
}
