package com.example.mobilephone.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.example.mobilephone.Models.StepSummary;
import com.example.mobilephone.Models.User;
import com.example.mobilephone.Repositories.StepSummaryRepository;

import java.util.Calendar;
import java.util.UUID;

import javax.inject.Inject;

public class StepSummaryViewModel extends ViewModel {
    private LiveData<StepSummary> stepSummary;
    private StepSummaryRepository stepSummaryRepo;

    // Instructs Dagger 2 to provide the StepSummaryRepository parameter.
    @Inject
    public StepSummaryViewModel(StepSummaryRepository stepSummaryRepo){
        this.stepSummaryRepo = stepSummaryRepo;

    }

    public void init(User user, int stepSummaryId) {
        if (this.stepSummary != null) {
            return;
        }

        stepSummary = stepSummaryRepo.getStepSummary(user, UUID.randomUUID().toString());
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
