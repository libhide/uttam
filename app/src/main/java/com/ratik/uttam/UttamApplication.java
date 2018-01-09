package com.ratik.uttam;

import android.app.Application;

import com.ratik.uttam.di.Injector;

/**
 * Created by Ratik on 17/10/17.
 */

public class UttamApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initDagger();
    }

    protected void initDagger() {
        Injector.initializeAppComponent(this);
        Injector.getAppComponent().inject(this);
    }
}
