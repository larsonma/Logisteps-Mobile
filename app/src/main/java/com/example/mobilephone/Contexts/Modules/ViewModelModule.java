package com.example.mobilephone.Contexts.Modules;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobilephone.Contexts.keys.ViewModelKey;
import com.example.mobilephone.ViewModels.FactoryViewModel;
import com.example.mobilephone.ViewModels.StepSummaryViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(StepSummaryViewModel.class)
    abstract ViewModel bindStepSummaryViewModel(StepSummaryViewModel stepSummaryViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(FactoryViewModel factory);
}
