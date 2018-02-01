package com.ratik.uttam.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
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
 * Created by Ratik on 23/10/17.
 */

public class GetPhotoJob extends JobService {
    private static final String TAG = GetPhotoJob.class.getSimpleName();

    @Inject
    UnsplashService service;

    @Inject
    DataStore dataStore;

    @Inject
    NotificationUtils notificationUtils;

    private Context context;

    public GetPhotoJob() {
        context = GetPhotoJob.this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Injector.getAppComponent().inject(this);
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        fetchPhoto(jobParameters);
        return true;
    }

    private void fetchPhoto(JobParameters parameters) {
        Log.i(TAG, "Fetching photo...");
        service.getRandomPhoto(BuildConfig.CLIENT_ID, Constants.Api.COLLECTIONS)
                .map(photo -> FetchUtils.makePhotoObject(this, photo))
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

                    // job is done
                    stopSelf();
                }, throwable -> Log.e(TAG, throwable.getMessage()));
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true;
    }
}
