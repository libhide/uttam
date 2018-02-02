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
import com.ratik.uttam.model.Photo;
import com.ratik.uttam.model.PhotoResponse;
import com.ratik.uttam.utils.FetchUtils;
import com.ratik.uttam.utils.NotificationUtils;
import com.ratik.uttam.utils.PrefUtils;
import com.ratik.uttam.utils.Utils;

import javax.inject.Inject;

import io.reactivex.Single;
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
    public boolean onStopJob(JobParameters jobParameters) {
        return true;
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        fetchPhoto();
        return true;
    }

    private void fetchPhoto() {
        Log.i(TAG, "Getting random photo...");
        service.getRandomPhoto(BuildConfig.CLIENT_ID, Constants.Api.COLLECTIONS)
                .flatMapSingle(response -> {
                    Single<String> full = FetchUtils.downloadWallpaperFull(context, response);
                    Single<String> regular = FetchUtils.downloadWallpaperRegular(context, response);
                    Single<String> thumb = FetchUtils.downloadWallpaperThumb(context, response);

                    return Single.zip(full, regular, thumb,
                            (s, s2, s3) -> getPhoto(response, s, s2, s3));
                })
                .flatMapCompletable(photo -> dataStore.putPhoto(photo))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onFetchSuccess, this::onFetchFailure);
    }

    private void onFetchSuccess() {
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
    }

    private void onFetchFailure(Throwable throwable) {
        Log.e(TAG, throwable.getMessage());
    }

    private Photo getPhoto(PhotoResponse response, String s, String s2, String s3) {
        return new Photo.Builder()
                .setPhotoUri(s)
                .setRegularPhotoUri(s2)
                .setThumbPhotoUri(s3)
                .setPhotoFullUrl(response.getUrls().getFullUrl())
                .setPhotoHtmlUrl(response.getLinks().getHtmlLink())
                .setPhotoDownloadUrl(response.getLinks().getDownloadLink())
                .setPhotographerUserName(response.getPhotographer().getUsername())
                .setPhotographerName(Utils.toTitleCase(response.getPhotographer().getName()))
                .build();
    }
}
