package com.ratik.uttam.di.module;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class FirebaseModule {

    @Provides
    @Singleton
    FirebaseRemoteConfig provideFirebaseRemoteConfig() {
        return FirebaseRemoteConfig.getInstance();
    }
}
