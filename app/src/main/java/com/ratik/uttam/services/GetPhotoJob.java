package com.ratik.uttam.services;

import android.app.WallpaperManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.ratik.uttam.BuildConfig;
import com.ratik.uttam.Constants;
import com.ratik.uttam.api.UnsplashService;
import com.ratik.uttam.data.DataStore;
import com.ratik.uttam.di.Injector;
import com.ratik.uttam.model.Photo;
import com.ratik.uttam.model.PhotoResponse;
import com.ratik.uttam.model.PhotoType;
import com.ratik.uttam.utils.BitmapUtils;
import com.ratik.uttam.utils.FetchUtils;
import com.ratik.uttam.utils.NotificationUtils;
import com.ratik.uttam.utils.Utils;

import java.io.IOException;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Ratik on 23/10/17.
 */

public class GetPhotoJob extends JobService {
    private static final String TAG = GetPhotoJob.class.getSimpleName();

    boolean isWorking = false;
    boolean jobCancelled = false;

    private Context context;
    private JobParameters jobParams;
    private String type = "";

    private WallpaperManager wallpaperManager;
    private CompositeDisposable compositeDisposable;

    @Inject
    UnsplashService service;

    @Inject
    DataStore dataStore;

    @Inject
    NotificationUtils notificationUtils;

    public GetPhotoJob() {
        context = GetPhotoJob.this;
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.hasExtra("type")) {
            type = intent.getStringExtra("type");
        } else {
            type = "";
        }

        if (type.equals("service")) {
            fetchPhoto();
            return START_STICKY;
        } else {
            // super class's behaviour
            // JobScheduler will handle calling onStartJob
            return super.onStartCommand(intent, flags, startId);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Injector.getAppComponent().inject(this);

        wallpaperManager = WallpaperManager.getInstance(context);
    }

    // Called by the Android system when it's time to run the job
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG, "Job started!");
        isWorking = true;

        // save jobParams
        jobParams = jobParameters;

        // do the work
        fetchPhoto();

        return isWorking;
    }

    // Called if the job was cancelled before being finished
    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.d(TAG, "Job cancelled before being completed.");

        jobCancelled = true;
        boolean needsReschedule = isWorking;
        jobFinished(jobParameters, needsReschedule);
        return needsReschedule;
    }

    private void fetchPhoto() {
        Log.i(TAG, "Getting random photo...");

        // If the job has been cancelled, stop working; the job will be rescheduled.
        if (jobCancelled)
            return;

        compositeDisposable.add(
                service.getRandomPhoto(BuildConfig.CLIENT_ID, Constants.Api.COLLECTIONS)
                .flatMapSingle(this::getPhotoSingle)
                .flatMapCompletable(photo -> {
                    Completable putCompletable = dataStore.putPhoto(photo);
                    if (dataStore.isAutoSetEnabled()) {
                        return doPostSavingStuff(putCompletable, photo);
                    } else {
                        return putCompletable;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onFetchSuccess, this::onFetchFailure)
        );
    }

    private Completable doPostSavingStuff(Completable dataStorePutCompletable, Photo photo) {
        return dataStorePutCompletable
                .andThen(getWallpaperPath(photo))
                .map(BitmapFactory::decodeFile)
                .flatMap(this::scaleWallpaper)
                .flatMapCompletable(this::setWall);
    }

    private Single<String> getWallpaperPath(Photo photo) {
        return Single.just(photo.getPhotoUri());
    }

    private Single<Photo> getPhotoSingle(PhotoResponse response) {
        Single<String> fullSingle = FetchUtils.downloadWallpaper(context,
                response, PhotoType.FULL);
        Single<String> regularSingle = FetchUtils.downloadWallpaper(context,
                response, PhotoType.REGULAR);
        Single<String> thumbSingle = FetchUtils.downloadWallpaper(context,
                response, PhotoType.THUMB);

        return Single.zip(fullSingle, regularSingle, thumbSingle,
                (fullUri, regularUri, thumbUri) -> {
                    Log.d(TAG, "Downloaded images");
                    return getPhoto(response, fullUri, regularUri, thumbUri);
                });
    }

    private void pushNotification() {
        notificationUtils.pushNewWallpaperNotification();
    }

    private Single<Bitmap> scaleWallpaper(Bitmap wallpaper) {
        Bitmap blank = BitmapUtils.createNewBitmap(
                wallpaperManager.getDesiredMinimumWidth(),
                wallpaperManager.getDesiredMinimumHeight()
        );
        return Single.fromCallable(() -> BitmapUtils.overlayIntoCentre(blank, wallpaper));
    }

    private Completable setWall(Bitmap bitmap) throws IOException {
        return Completable.fromAction(() -> wallpaperManager.setBitmap(bitmap));
    }

    private void onFetchSuccess() {
        Log.i(TAG, "Photo saved successfully!");
        pushNotification();
        compositeDisposable.dispose();

        // stop job / service
        if (!type.equals("service")) {
            // JOB
            Log.d(TAG, "Job finished!");
            isWorking = false;
            boolean needsReschedule = false;
            jobFinished(jobParams, needsReschedule);
        } else {
            // SERVICE
            stopSelf();
        }
    }

    private void onFetchFailure(Throwable throwable) {
        Log.e(TAG, throwable.getMessage());
    }

    private Photo getPhoto(PhotoResponse response, String fullUri, String regularUri,
                           String thumbUri) {

        return new Photo.Builder()
                .setId(response.getId())
                .setPhotoUri(fullUri)
                .setRegularPhotoUri(regularUri)
                .setThumbPhotoUri(thumbUri)
                .setPhotoFullUrl(response.getUrls().getFullUrl())
                .setPhotoHtmlUrl(response.getLinks().getHtmlLink())
                .setPhotoDownloadUrl(response.getLinks().getDownloadLink())
                .setPhotographerUserName(response.getPhotographer().getUsername())
                .setPhotographerName(Utils.toTitleCase(response.getPhotographer().getName()))
                .build();
    }
}
