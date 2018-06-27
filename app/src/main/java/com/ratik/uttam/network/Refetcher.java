package com.ratik.uttam.network;

import android.app.WallpaperManager;
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
import com.ratik.uttam.utils.Utils;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class Refetcher {
    private static final String TAG = Refetcher.class.getSimpleName();

    private Context context;
    private CompositeDisposable compositeDisposable;
    private WallpaperManager wallpaperManager;

    private Refetcher.Callback callback;

    public interface Callback {
        void refetchComplete();
    }

    @Inject
    UnsplashService service;

    @Inject
    PhotoStore photoStore;

    @Inject
    PrefStore prefStore;

    public Refetcher(Context context) {
        this.context = context;
        compositeDisposable = new CompositeDisposable();
        wallpaperManager = WallpaperManager.getInstance(context);
        Injector.getAppComponent().inject(this);
    }

    public void setRefetchCallback(Refetcher.Callback callback) {
        this.callback = callback;
    }

    public void doRefetch(){
        Log.i(TAG, "Fetching new photo...");
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

    public void cleanup() {
        compositeDisposable.dispose();
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

    private Completable setWall(Bitmap bitmap) {
        return Completable.fromAction(() -> wallpaperManager.setBitmap(bitmap));
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
