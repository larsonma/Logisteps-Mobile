package com.example.mobilephone.Contexts.Modules;

import com.example.mobilephone.Activities.LoginActivity;
import com.example.mobilephone.Activities.MainActivity;
import com.example.mobilephone.Activities.RegisterActivity;

import dagger.Module;

@Module
public abstract class ActivityModule {
    abstract MainActivity contributeMainActivity();
    abstract LoginActivity contributeLoginActivity();
    abstract RegisterActivity contributeRegisterActivity();
}
