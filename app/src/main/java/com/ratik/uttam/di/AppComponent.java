package com.ratik.uttam.di;

import com.ratik.uttam.UttamApplication;
import com.ratik.uttam.data.DatabaseRealm;
import com.ratik.uttam.data.impl.PhotoRepositoryImpl;
import com.ratik.uttam.di.module.ApiModule;
import com.ratik.uttam.di.module.AppContextModule;
import com.ratik.uttam.di.module.RepositoryModule;
import com.ratik.uttam.services.GetPhotoService;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Ratik on 17/10/17.
 */

@Singleton
@Component(modules = {AppContextModule.class, ApiModule.class, RepositoryModule.class})
public interface AppComponent {

    // app
    void inject(UttamApplication application);

    // api
    void inject(GetPhotoService service);

    // repository
    void inject(DatabaseRealm databaseRealm);
    void inject(PhotoRepositoryImpl photoRepository);
}