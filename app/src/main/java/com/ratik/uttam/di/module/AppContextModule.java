package com.ratik.uttam.di.module;

import android.app.WallpaperManager;
import android.content.Context;

import com.ratik.uttam.UttamApplication;
import com.ratik.uttam.api.UnsplashService;
import com.ratik.uttam.data.PhotoStore;
import com.ratik.uttam.data.PrefStore;
import com.ratik.uttam.network.FetchService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Ratik on 17/10/17.
 */

@Module
public class AppContextModule {
    private final UttamApplication application;

    public AppContextModule(UttamApplication application) {
        this.application = application;
    }

    @Provides
    @Singleton
    public UttamApplication application() {
        return application;
    }

    @Provides
    @Singleton
    public Context applicationContext() {
        return application.getApplicationContext();
    }

    @Provides
    @Singleton
    public WallpaperManager provideWallpaperManager(Context context) {
        return WallpaperManager.getInstance(context);
    }

    @Provides
    public FetchService provideFetchService(Context context, UnsplashService service,
                                     PhotoStore photoStore, PrefStore prefStore,
                                     WallpaperManager wallpaperManager) {
        return new FetchService(context, service, photoStore, prefStore, wallpaperManager);
    }
}
