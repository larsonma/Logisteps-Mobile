package com.example.mobilephone.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.mobilephone.Models.StepSummary;
import com.example.mobilephone.Models.User;
import com.example.mobilephone.Repositories.StepSummaryRepository;

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
        stepSummary = stepSummaryRepo.getStepSummary(user, stepSummaryId);
    }

    public LiveData<StepSummary> getStepSummary() {
        return this.stepSummary;
    }
}
