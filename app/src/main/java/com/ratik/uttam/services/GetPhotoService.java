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
import com.ratik.uttam.utils.BitmapUtils;
import com.ratik.uttam.utils.NotificationUtils;
import com.ratik.uttam.utils.PrefUtils;
import com.ratik.uttam.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
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
                .map(photoResponse -> makePhotoObject(photoResponse))
                .map(photo -> BitmapUtils.scaleBitmap(this, photo))
                .map(photo -> dataStore.putPhoto(photo))
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

    private Bitmap downloadAndSaveWallpaperImage(PhotoResponse photoResponse) throws IOException {
        try {
            URL url = new URL(photoResponse.getUrls().getFullUrl());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream inputStream = connection.getInputStream();

            File imageFile = createFile();
            FileOutputStream output = new FileOutputStream(imageFile);
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, len);
            }
            return BitmapFactory.decodeFile(imageFile.getAbsolutePath());
        } catch (IOException e) {
            throw new IOException("Error downloading wallpaper image");
        }
    }

    private File createFile() {
        File directory;
        directory = context.getDir("Uttam", Context.MODE_PRIVATE);
        if (!directory.exists() && !directory.mkdirs()) {
            Log.e("PhotoSaver", "Error creating directory " + directory);
        }

        return new File(directory, Constants.General.WALLPAPER_FILE_NAME);
    }

    private Photo makePhotoObject(PhotoResponse photoResponse) throws IOException {
        Bitmap wallpaperImage = downloadAndSaveWallpaperImage(photoResponse);
        return new Photo.Builder()
                .setPhoto(wallpaperImage)
                .setPhotoFullUrl(photoResponse.getUrls().getFullUrl())
                .setPhotoHtmlUrl(photoResponse.getLinks().getHtmlLink())
                .setPhotoDownloadUrl(photoResponse.getLinks().getDownloadLink())
                .setPhotographerUserName(photoResponse.getPhotographer().getUsername())
                .setPhotographerName(Utils.toTitleCase(photoResponse.getPhotographer().getName()))
                .build();
    }
}
