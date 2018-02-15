package com.ratik.uttam.di.module;

import com.ratik.uttam.Constants;
import com.ratik.uttam.api.UnsplashService;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Ratik on 17/10/17.
 */

@Module
public class ApiModule {

    @Provides
    public Retrofit provideRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(Constants.Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    @Provides
    public UnsplashService provideUnsplashService(Retrofit retrofit) {
        return retrofit.create(UnsplashService.class);
    }
}
