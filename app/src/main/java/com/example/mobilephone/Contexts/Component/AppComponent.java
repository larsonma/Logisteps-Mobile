package com.example.mobilephone.Contexts.Component;

import android.app.Application;

import com.example.mobilephone.App;
import com.example.mobilephone.Contexts.Modules.ActivityModule;
import com.example.mobilephone.Contexts.Modules.AppModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(modules = {AndroidSupportInjectionModule.class, ActivityModule.class, AppModule.class})
public interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);
        AppComponent build();
    }

    void inject(App app);
}
