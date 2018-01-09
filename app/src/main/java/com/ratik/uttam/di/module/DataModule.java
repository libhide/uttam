package com.ratik.uttam.di.module;

import android.content.Context;

import com.ratik.uttam.data.DataStore;
import com.ratik.uttam.data.impl.DataStoreImpl;
import com.ratik.uttam.utils.PhotoSaver;

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
    public DataStore providesDataStore() {
        return new DataStoreImpl();
    }

    @Provides
    @Singleton
    public PhotoSaver providesPhotoSaver(Context context) {
        return new PhotoSaver(context);
    }
}
