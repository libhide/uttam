package com.ratik.uttam.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ratik.uttam.BuildConfig;
import com.ratik.uttam.Constants;
import com.ratik.uttam.api.UnsplashService;
import com.ratik.uttam.data.DataStore;
import com.ratik.uttam.di.Injector;
import com.ratik.uttam.model.Photo;
import com.ratik.uttam.model.PhotoResponse;
import com.ratik.uttam.model.PhotoType;
import com.ratik.uttam.utils.FetchUtils;
import com.ratik.uttam.utils.Utils;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class RefetchService extends Service {

    public class LocalBinder extends Binder {
        public RefetchService getService() {
            return RefetchService.this;
        }
    }

    public interface Callback {
        void refetchComplete();
    }

    public static final String TAG = RefetchService.class.getSimpleName();

    private Context context;
    private CompositeDisposable compositeDisposable;
    private IBinder localBinder;
    private Callback callback;

    @Inject
    UnsplashService service;

    @Inject
    DataStore dataStore;

    public RefetchService() {

    }

    public void setRefetchCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = RefetchService.this;
        compositeDisposable = new CompositeDisposable();
        localBinder = new LocalBinder();
        Injector.getAppComponent().inject(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return localBinder;
    }

    public void fetchPhoto() {
        Log.i(TAG, "Fetching new photo...");
        compositeDisposable.add(
                service.getRandomPhoto(BuildConfig.CLIENT_ID, Constants.Api.COLLECTIONS)
                        .flatMapSingle(this::getPhotoSingle)
                        .flatMapCompletable(photo -> dataStore.putPhoto(photo))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::onFetchSuccess, this::onFetchFailure)
        );
    }

    // Helper methods
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

    private void onFetchSuccess() {
        Log.i(TAG, "Photo saved successfully!");
        if (callback != null) {
            callback.refetchComplete();
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

    @Override
    public boolean onUnbind(Intent intent) {
        compositeDisposable.dispose();
        return super.onUnbind(intent);
    }
}
