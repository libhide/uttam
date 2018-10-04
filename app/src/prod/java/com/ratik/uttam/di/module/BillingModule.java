package com.ratik.uttam.di.module;

import android.content.Context;

import com.ratik.uttam.Constants;

import org.solovyev.android.checkout.Billing;

import javax.annotation.Nonnull;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class BillingModule {
    @Provides
    Billing.Configuration provideBillingConfiguration() {
        return new Billing.DefaultConfiguration() {
            @Nonnull
            @Override
            public String getPublicKey() {
                return Constants.Billing.BILLING_PUBLIC_KEY;
            }
        };
    }

    @Provides
    @Singleton
    Billing provideBilling(Context context, Billing.Configuration configuration) {
        return new Billing(context, configuration);
    }
}
