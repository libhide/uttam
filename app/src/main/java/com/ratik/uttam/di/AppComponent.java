package com.ratik.uttam.di;

import com.ratik.uttam.UttamApplication;
import com.ratik.uttam.data.DatabaseRealm;
import com.ratik.uttam.data.impl.PhotoRepositoryImpl;
import com.ratik.uttam.di.module.ApiModule;
import com.ratik.uttam.di.module.AppContextModule;
import com.ratik.uttam.di.module.PresenterModule;
import com.ratik.uttam.di.module.RepositoryModule;
import com.ratik.uttam.services.GetPhotoJob;
import com.ratik.uttam.services.GetPhotoService;
import com.ratik.uttam.ui.hero.HeroActivity;
import com.ratik.uttam.ui.main.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Ratik on 17/10/17.
 */

@Singleton
@Component(modules = {AppContextModule.class, PresenterModule.class,
                      ApiModule.class, RepositoryModule.class})
public interface AppComponent {

    void inject(UttamApplication application);

    void inject(GetPhotoService service);
    void inject(GetPhotoJob job);

    void inject(DatabaseRealm databaseRealm);
    void inject(PhotoRepositoryImpl photoRepository);

    void inject(HeroActivity activity);
    void inject(MainActivity activity);
}