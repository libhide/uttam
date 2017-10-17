package com.ratik.uttam.di.module;

import com.ratik.uttam.data.DatabaseRealm;
import com.ratik.uttam.data.PhotoRepository;
import com.ratik.uttam.data.impl.PhotoRepositoryImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Ratik on 17/10/17.
 */

@Module
public class RepositoryModule {

    @Provides
    @Singleton
    public PhotoRepository provideMessageRepository() {
        return new PhotoRepositoryImpl();
    }

    @Provides
    @Singleton
    public DatabaseRealm provideDatabaseRealm() {
        return new DatabaseRealm();
    }
}
