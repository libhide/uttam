package com.ratik.uttam.di;

import com.ratik.uttam.UttamApplication;
import com.ratik.uttam.di.module.ApiModule;
import com.ratik.uttam.di.module.AppContextModule;
import com.ratik.uttam.di.module.DataModule;
import com.ratik.uttam.di.module.PresenterModule;

import java.util.Objects;

public class Injector {

    private static AppComponent appComponent;

    private Injector() {
    }

    public static void initializeAppComponent(UttamApplication application) {
        appComponent = DaggerAppComponent.builder()
                .appContextModule(new AppContextModule(application))
                .apiModule(new ApiModule())
                .dataModule(new DataModule())
                .presenterModule(new PresenterModule())
                .build();
    }

    public static AppComponent getAppComponent() {
        Objects.requireNonNull(appComponent, "appComponent is null");
        return appComponent;
    }
}
