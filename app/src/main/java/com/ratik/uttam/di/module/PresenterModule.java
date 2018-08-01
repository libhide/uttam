package com.ratik.uttam.di.module;

import com.ratik.uttam.data.PhotoStore;
import com.ratik.uttam.network.FetchService;
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
    public MainContract.Presenter provideMainPresenter(PhotoStore photoStore, FetchService fetchService) {
        return new MainPresenterImpl(photoStore, fetchService);
    }
}
