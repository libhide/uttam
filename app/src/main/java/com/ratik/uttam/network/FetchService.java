package com.ratik.uttam.network;

import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.ratik.uttam.BuildConfig;
import com.ratik.uttam.R;
import com.ratik.uttam.data.UnsplashApi;
import com.ratik.uttam.data.PhotoStore;
import com.ratik.uttam.data.PrefStore;
import com.ratik.uttam.domain.model.Photo;
import com.ratik.uttam.data.model.PhotoApiModel;
import com.ratik.uttam.domain.model.PhotoType;
import com.ratik.uttam.util.StringExtensionsKt;

import java.io.IOException;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class FetchService {
    private static final String TAG = FetchService.class.getSimpleName();
    private final String PARAM_COLLECTIONS = "unsplash_collections";

    private UnsplashApi service;
    private PhotoStore photoStore;
    private PrefStore prefStore;
    private WallpaperManager wallpaperManager;
    private DownloadService downloadService;
    private FirebaseRemoteConfig remoteConfig;

    @Inject
    public FetchService(DownloadService downloadService, UnsplashApi service, PhotoStore photoStore, PrefStore prefStore, WallpaperManager wallpaperManager, FirebaseRemoteConfig remoteConfig) {
        this.downloadService = downloadService;
        this.service = service;
        this.photoStore = photoStore;
        this.prefStore = prefStore;
        this.wallpaperManager = wallpaperManager;
        this.remoteConfig = remoteConfig;
        this.remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);
    }

    public Completable getFetchPhotoCompletable() {
        return getRemoteConfigFetchSingle().flatMapCompletable(this::getFetchPhotoCompletable);
    }

    private Single<String> getRemoteConfigFetchSingle() {
        remoteConfig.fetch().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                remoteConfig.fetchAndActivate();
            }
        });
        return Single.just(remoteConfig.getString(PARAM_COLLECTIONS));
    }

    private Completable getFetchPhotoCompletable(String collections) {
        Observable<PhotoApiModel> photoResponseObservable = service.getRandomPhoto(BuildConfig.CLIENT_ID, collections, prefStore.getDesiredWallpaperWidth(), prefStore.getDesiredWallpaperHeight());

        return photoResponseObservable.flatMapSingle(this::getPhotoSingle).flatMapSingle(this::increaseDownloadCountForWallpaper).flatMapCompletable(photo -> {
            Completable putCompletable = photoStore.putPhoto(photo);
            if (prefStore.isAutoSetEnabled()) {
                return getPostSaveSetWallpaperCompletable(putCompletable, photo);
            } else {
                return putCompletable;
            }
        });
    }

    private Single<Photo> getPhotoSingle(PhotoApiModel response) throws IOException {
        Single<String> fullSingle = downloadService.downloadWallpaper(response, PhotoType.FULL);
        Single<String> regularSingle = downloadService.downloadWallpaper(response, PhotoType.REGULAR);
        Single<String> thumbSingle = downloadService.downloadWallpaper(response, PhotoType.THUMB);

        return Single.zip(fullSingle, regularSingle, thumbSingle, (fullUri, regularUri, thumbUri) -> {
            Log.i(TAG, "Images downloaded");
            return getPhoto(response, fullUri, regularUri, thumbUri);
        });
    }

    private Single<Photo> increaseDownloadCountForWallpaper(Photo photo) throws IOException {
        String downloadEndpoint = photo.getPhotoDownloadEndpoint();
        downloadEndpoint += "?client_id=" + BuildConfig.CLIENT_ID;

        if (!downloadEndpoint.isEmpty()) {
            Request request = new Request.Builder().url(downloadEndpoint).build();
            new OkHttpClient().newCall(request).execute();
            Log.i(TAG, "Download count for " + photo.getPhotoDownloadUrl() + " increased.");
        }

        return Single.just(photo);
    }

    private Completable getPostSaveSetWallpaperCompletable(Completable photoStorePutCompletable, Photo photo) {
        return photoStorePutCompletable.andThen(getWallpaperPath(photo)).map(BitmapFactory::decodeFile).flatMapCompletable(this::setWall);
    }

    private Single<String> getWallpaperPath(Photo photo) {
        return Single.just(photo.getFullPhotoUri());
    }

    private Completable setWall(Bitmap bitmap) {
        return Completable.fromAction(() -> wallpaperManager.setBitmap(bitmap));
    }

    private Photo getPhoto(PhotoApiModel response, String fullUri, String regularUri, String thumbUri) {
        return new Photo(
                response.getId(),
                fullUri,
                regularUri,
                thumbUri,
                StringExtensionsKt.toTitleCase(response.getPhotographer().getName()),
                response.getPhotographer().getUsername(),
                response.getUrls().getFullUrl(),
                response.getLinks().getDownloadLink(),
                response.getLinks().getDownloadEndpoint(),
                response.getLinks().getHtmlLink()
        );
    }
}
