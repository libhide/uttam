package com.ratik.uttam.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ratik.uttam.BuildConfig;
import com.ratik.uttam.Constants;
import com.ratik.uttam.api.UnsplashService;
import com.ratik.uttam.data.DataStore;
import com.ratik.uttam.di.Injector;
import com.ratik.uttam.utils.FetchUtils;
import com.ratik.uttam.utils.NotificationUtils;
import com.ratik.uttam.utils.PrefUtils;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Ratik on 04/03/16.
 */
public class GetPhotoService extends Service {
    private static final String TAG = GetPhotoService.class.getSimpleName();

    @Inject
    UnsplashService service;

    @Inject
    DataStore dataStore;

    @Inject
    NotificationUtils notificationUtils;

    private Context context;

    public GetPhotoService() {
        context = GetPhotoService.this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Injector.getAppComponent().inject(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        fetchPhoto();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void fetchPhoto() {
        Log.i(TAG, "Getting random photo...");
        service.getRandomPhoto(BuildConfig.CLIENT_ID, Constants.Api.COLLECTIONS)
                .map(photoResponse -> FetchUtils.makePhotoObject(this, photoResponse))
                .map(photo -> dataStore.putPhoto(photo))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((completable) -> {
                    // Notify User
                    notificationUtils.pushNewWallpaperNotification();

                    // If user wants auto-magical setting, set the wallpaper
                    if (PrefUtils.shouldSetWallpaperAutomatically(context)) {
                        // todo: figure out automagical setting
                        // WallpaperManager.getInstance(context).setBitmap(image);
                    }

                    Log.i(TAG, "Photo saved successfully!");

                    // stop service
                    stopSelf();
                }, throwable -> Log.e(TAG, throwable.getMessage()));
    }
}
