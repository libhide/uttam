package com.ratik.uttam.di.module;

import android.content.Context;

import com.ratik.uttam.utils.NotificationUtils;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Ratik on 09/01/18.
 */
@Module
public class UtilsModule {

    @Provides
    @Singleton
    public NotificationUtils providesNotificationUtils(Context context) {
        return new NotificationUtils(context);
    }
}
