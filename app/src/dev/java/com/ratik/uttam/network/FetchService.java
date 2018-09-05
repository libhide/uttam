package com.ratik.uttam.network;

import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.ratik.uttam.BuildConfig;
import com.ratik.uttam.Constants;
import com.ratik.uttam.api.UnsplashService;
import com.ratik.uttam.data.PhotoStore;
import com.ratik.uttam.data.PrefStore;
import com.ratik.uttam.model.Photo;
import com.ratik.uttam.model.PhotoResponse;
import com.ratik.uttam.model.PhotoType;
import com.ratik.uttam.utils.Utils;

import java.io.IOException;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

public class FetchService {
    private static final String TAG = FetchService.class.getSimpleName();

    private UnsplashService service;
    private PhotoStore photoStore;
    private PrefStore prefStore;
    private WallpaperManager wallpaperManager;
    private DownloadService downloadService;

    @Inject
    public FetchService(DownloadService downloadService, UnsplashService service, PhotoStore photoStore,
                        PrefStore prefStore, WallpaperManager wallpaperManager) {
        this.downloadService = downloadService;
        this.service = service;
        this.photoStore = photoStore;
        this.prefStore = prefStore;
        this.wallpaperManager = wallpaperManager;
    }

    public Completable getFetchPhotoCompletable() {
        Observable<PhotoResponse> photoResponseObservable = service.getRandomPhoto(
                BuildConfig.CLIENT_ID,
                Constants.Api.COLLECTIONS,
                prefStore.getDesiredWallpaperWidth(),
                prefStore.getDesiredWallpaperHeight()
        );

        return photoResponseObservable
                .flatMapSingle(this::getPhotoSingle)
                .flatMapCompletable(photo -> {
                    Completable putCompletable = photoStore.putPhoto(photo);
                    if (prefStore.isAutoSetEnabled()) {
                        return getPostSaveSetWallpaperCompletable(putCompletable, photo);
                    } else {
                        return putCompletable;
                    }
                });
    }

    private Single<Photo> getPhotoSingle(PhotoResponse response) throws IOException {
        Single<String> fullSingle = downloadService.downloadWallpaper(response, PhotoType.FULL);
        Single<String> regularSingle = downloadService.downloadWallpaper(response, PhotoType.REGULAR);
        Single<String> thumbSingle = downloadService.downloadWallpaper(response, PhotoType.THUMB);

        return Single.zip(fullSingle, regularSingle, thumbSingle,
                (fullUri, regularUri, thumbUri) -> {
                    Log.i(TAG, "Images downloaded");
                    return getPhoto(response, fullUri, regularUri, thumbUri);
                });
    }

    private Completable getPostSaveSetWallpaperCompletable(Completable photoStorePutCompletable, Photo photo) {
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
