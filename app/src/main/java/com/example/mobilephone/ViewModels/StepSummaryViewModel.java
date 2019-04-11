package com.example.mobilephone.ViewModels;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.example.mobilephone.Models.StepSummary;
import com.example.mobilephone.Models.User;
import com.example.mobilephone.Repositories.StepSummaryRepository;
import com.example.mobilephone.Repositories.UserRepository;

import java.util.Calendar;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

public class StepSummaryViewModel extends AndroidViewModel {
    private LiveData<StepSummary> stepSummary;
    @Inject StepSummaryRepository stepSummaryRepo;
    @Inject UserRepository userRepository;

    // Instructs Dagger 2 to provide the StepSummaryRepository parameter.
    @Inject
    public StepSummaryViewModel(@NonNull final Application application){
        super(application);
    }

    public void init() {
        if (this.stepSummary != null) {
            return;
        }
        SharedPreferences sharedPreferences = getApplication().getSharedPreferences("userCredentials", Context.MODE_PRIVATE);

        try {
            User currentUser = userRepository.getUser(sharedPreferences.getString("username", ""),
                    sharedPreferences.getString("password", ""));

            stepSummary = stepSummaryRepo.getStepSummary(currentUser, UUID.randomUUID().toString());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public LiveData<StepSummary> getStepSummary() {
        return this.stepSummary;
    }

    public int getProjectedSteps() {
        int hoursToMidnight = hoursToMidnight();
        StepSummary stepSummary = this.stepSummary.getValue();

        return stepSummary.getSteps() + (int)(hoursToMidnight * stepSummary.getStepsPerHour());
    }

    private int hoursToMidnight() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long msToMidnight = calendar.getTimeInMillis() - System.currentTimeMillis();
        return (int) (msToMidnight / 1000 / 60 / 60);
    }
}
