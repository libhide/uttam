package com.ratik.uttam.services;

import android.app.WallpaperManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.ratik.uttam.BuildConfig;
import com.ratik.uttam.Constants;
import com.ratik.uttam.api.UnsplashService;
import com.ratik.uttam.data.PhotoStore;
import com.ratik.uttam.data.PrefStore;
import com.ratik.uttam.di.Injector;
import com.ratik.uttam.model.Photo;
import com.ratik.uttam.model.PhotoResponse;
import com.ratik.uttam.model.PhotoType;
import com.ratik.uttam.utils.FetchUtils;
import com.ratik.uttam.utils.NotificationUtils;
import com.ratik.uttam.utils.Utils;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Ratik on 23/10/17.
 */

public class GetPhotoJob extends JobService {
    private static final String TAG = GetPhotoJob.class.getSimpleName();

    @Inject
    UnsplashService service;

    @Inject
    PhotoStore photoStore;

    @Inject
    PrefStore prefStore;

    @Inject
    NotificationUtils notificationUtils;

    private Context context;
    private JobParameters jobParams;
    private WallpaperManager wallpaperManager;
    private CompositeDisposable compositeDisposable;

    boolean isWorking = false;
    boolean jobCancelled = false;

    public GetPhotoJob() {
        context = GetPhotoJob.this;
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Injector.getAppComponent().inject(this);
        wallpaperManager = WallpaperManager.getInstance(context);
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        isWorking = true;
        jobParams = jobParameters;
        fetchPhoto();
        return isWorking;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        jobCancelled = true;
        boolean needsReschedule = isWorking;
        jobFinished(jobParameters, needsReschedule);
        return needsReschedule;
    }

    private void fetchPhoto() {
        Log.i(TAG, "Fetching photo...");

        // If the job has been cancelled, stop working; the job will be rescheduled.
        if (jobCancelled)
            return;

        Observable<PhotoResponse> photoResponseObservable = service.getRandomPhoto(
                BuildConfig.CLIENT_ID,
                Constants.Api.COLLECTIONS,
                prefStore.getDesiredWallpaperWidth(),
                prefStore.getDesiredWallpaperHeight()
        );
        compositeDisposable.add(
                photoResponseObservable
                        .flatMapSingle(this::getPhotoSingle)
                        .flatMapCompletable(photo -> {
                            Completable putCompletable = photoStore.putPhoto(photo);
                            if (prefStore.isAutoSetEnabled()) {
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

    private Completable doPostSavingStuff(Completable photoStorePutCompletable,
                                          Photo photo) {
        return photoStorePutCompletable
                .andThen(getWallpaperPath(photo))
                .map(BitmapFactory::decodeFile)
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
                    Log.i(TAG, "Images downloaded");
                    return getPhoto(response, fullUri, regularUri, thumbUri);
                });
    }

    private void pushNotification() {
        notificationUtils.pushNewWallpaperNotification();
    }

    private Completable setWall(Bitmap bitmap) {
        return Completable.fromAction(() -> wallpaperManager.setBitmap(bitmap));
    }

    private void onFetchSuccess() {
        Log.i(TAG, "Photo saved successfully!");
        pushNotification();
        isWorking = false;
        compositeDisposable.dispose();

        jobFinished(jobParams, false);
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
