package com.ratik.uttam;

import android.app.Application;

import com.ratik.uttam.data.DatabaseRealm;
import com.ratik.uttam.di.Injector;

import javax.inject.Inject;

/**
 * Created by Ratik on 17/10/17.
 */

public class UttamApplication extends Application {

    @Inject
    DatabaseRealm databaseRealm;

    @Override
    public void onCreate() {
        super.onCreate();
        initDagger();
        initRealm();
    }

    protected void initDagger() {
        Injector.initializeAppComponent(this);
        Injector.getAppComponent().inject(this);
    }

    protected void initRealm() {
        databaseRealm.setup();
    }
}
