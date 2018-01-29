package com.ratik.uttam.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.ratik.uttam.utils.NotificationUtils;
import com.ratik.uttam.utils.PrefUtils;
import com.ratik.uttam.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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
                .map(photoResponse -> photoResponse.body())
                .map(photo -> makePhotoObject(photo))
                .doOnNext(photo -> dataStore.putPhoto(photo))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        (completable) -> {
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
                        },
                        (throwable -> {
                            Log.e(TAG, throwable.getMessage());
                        })
                );
    }

    private Bitmap downloadWallpaperImage(PhotoResponse photo) throws IOException {
        try {
            URL url = new URL(photo.getUrls().getFullUrl());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (MalformedURLException e) {
            throw new IOException("Malformed URL for wallpaper image");
        } catch (IOException e) {
            throw new IOException("Error downloading wallpaper image");
        }
    }

    private Photo makePhotoObject(PhotoResponse photo) throws IOException {
        Bitmap wallpaperImage = downloadWallpaperImage(photo);
        if (wallpaperImage != null) {
            return new Photo.Builder()
                    .setPhoto(wallpaperImage)
                    .setPhotoFullUrl(photo.getUrls().getFullUrl())
                    .setPhotoHtmlUrl(photo.getLinks().getHtmlLink())
                    .setPhotoDownloadUrl(photo.getLinks().getDownloadLink())
                    .setPhotographerUserName(photo.getPhotographer().getUsername())
                    .setPhotographerName(Utils.toTitleCase(photo.getPhotographer().getName()))
                    .build();
        } else {
            // todo: do something here
            return null;
        }
    }
}
