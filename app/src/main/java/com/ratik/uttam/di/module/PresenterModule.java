package com.ratik.uttam.di.module;

import com.ratik.uttam.api.UnsplashService;
import com.ratik.uttam.data.DataStore;
import com.ratik.uttam.ui.main.MainContract;
import com.ratik.uttam.ui.main.MainPresenterImpl;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Ratik on 17/10/17.
 */

@Module
public class PresenterModule {

    @Provides
    public MainContract.Presenter provideMainPresenter(DataStore dataStore, UnsplashService service) {
        return new MainPresenterImpl(dataStore);
    }
}
