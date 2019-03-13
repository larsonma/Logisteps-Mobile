package com.example.mobilephone.Contexts.Modules;

import com.example.mobilephone.Activities.LoginActivity;
import com.example.mobilephone.Activities.MainActivity.MainActivity;
import com.example.mobilephone.Activities.RegisterActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityModule {
    @ContributesAndroidInjector
    abstract MainActivity contributeMainActivity();
    @ContributesAndroidInjector
    abstract LoginActivity contributeLoginActivity();
    @ContributesAndroidInjector
    abstract RegisterActivity contributeRegisterActivity();
}
