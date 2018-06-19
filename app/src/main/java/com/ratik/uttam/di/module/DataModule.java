package com.ratik.uttam.di.module;

import android.content.SharedPreferences;

import com.ratik.uttam.data.PhotoStore;
import com.ratik.uttam.data.PrefStore;
import com.ratik.uttam.data.impl.PhotoStoreImpl;
import com.ratik.uttam.data.impl.PrefStoreImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Ratik on 17/10/17.
 */

@Module
public class DataModule {

    @Provides
    @Singleton
    public PhotoStore providesPhotoStore(SharedPreferences prefs) {
        return new PhotoStoreImpl(prefs);
    }

    @Provides
    @Singleton
    public PrefStore providesPrefStore(SharedPreferences prefs) {
        return new PrefStoreImpl(prefs);
    }
}
