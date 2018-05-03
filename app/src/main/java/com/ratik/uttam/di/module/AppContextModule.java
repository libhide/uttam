package com.ratik.uttam.di.module;

import android.content.Context;

import com.ratik.uttam.UttamApplication;

import org.solovyev.android.checkout.Billing;

import javax.annotation.Nonnull;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Ratik on 17/10/17.
 */

@Module
public class AppContextModule {
    private final UttamApplication application;

    public AppContextModule(UttamApplication application) {
        this.application = application;
    }

    @Provides
    @Singleton
    public UttamApplication application() {
        return application;
    }

    @Provides
    @Singleton
    public Context applicationContext() {
        return application.getApplicationContext();
    }

    @Provides
    Billing.Configuration provideBillingConfiguration() {
        return new Billing.DefaultConfiguration() {
            @Nonnull
            @Override
            public String getPublicKey() {
                // TODO: learn to encrypt the key
                return "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAoueA0HGGz3Y0XE6WmVT7jt4e7cDhMWoH99mpAiu4uJjHOMR3dlpJOeK4GwayFKiemVHdQSu7hxNzVBYZdX09VWCrPGeFJfme6lMXP9QmWJKyyivPuzUW4w/0eEl908hJEJIgyJkR89LSfNVEmwrVo3mM4ZI6hXks/libNpaKiny1y2LLWlEM3Ue9nVqhj68G/iWIrF1Zk6h7K3lvF4mj7MEnTsFbVZzuGtFFOuibQXd9d1Jib3HCrApRhYXnhsi3yszCv4BErsBG2F55mJmpp7fGD37RIlTIx/PR+xOISjzliwM010REDmVpNFVRzep27Q2La3GAv4eCFf7tqeCSwQIDAQAB";
            }
        };
    }

    @Provides
    @Singleton
    Billing provideBilling(Context context, Billing.Configuration configuration) {
        return new Billing(context, configuration);
    }
}
