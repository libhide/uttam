package com.ratik.uttam.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ratik.uttam.Keys;
import com.ratik.uttam.api.UnsplashService;
import com.ratik.uttam.data.PhotoRepository;
import com.ratik.uttam.di.Injector;
import com.ratik.uttam.model.Photo;
import com.ratik.uttam.model._Photo;
import com.ratik.uttam.utils.FetchUtils;
import com.ratik.uttam.utils.FileUtils;
import com.ratik.uttam.utils.NotificationUtils;
import com.ratik.uttam.utils.PrefUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Ratik on 04/03/16.
 */
public class GetPhotoService extends Service {
    private static final String TAG = GetPhotoService.class.getSimpleName();

    @Inject
    UnsplashService service;

    @Inject
    PhotoRepository repository;

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
        getRandomPhoto();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void getRandomPhoto() {
        Log.d(TAG, "Getting random photo...");
        service
                .getRandomPhoto(Keys.CLIENT_ID, FetchUtils.getRandomCategory())
                .enqueue(new Callback<Photo>() {
                    @Override
                    public void onResponse(Call<Photo> call, Response<Photo> response) {
                        if (response.isSuccessful()) {
                            // Photo yay!
                            Photo photo = response.body();
                            savePhoto(photo);
                        }
                    }

                    @Override
                    public void onFailure(Call<Photo> call, Throwable t) {
                        Log.e(TAG, "onFailure: " + t.getMessage());
                    }
                });
    }

    private void savePhoto(Photo photo) {
        Observable.fromCallable(() -> {
            URL url = new URL(photo.getUrls().getFullUrl());
            try {
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                return BitmapFactory.decodeStream(input);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
                return null;
            }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe((image) -> {
            if (image != null) {
                // Save photo to internal storage
                FileUtils.clearFile(context, "wallpaper.png");
                FileUtils.saveImage(context, image, "wallpaper", "png");

                // Save photo data to Realm
                _Photo p = new _Photo();
                p.setPhotographerName(photo.getPhotographer().getName());
                p.setPhotographerUserName(photo.getPhotographer().getUsername());
                p.setPhotoDownloadUrl(photo.getLinks().getDownloadLink());
                p.setPhotoHtmlUrl(photo.getLinks().getHtmlLink());
                p.setPhotoFullUrl(photo.getUrls().getFullUrl());
                // todo: make dynamic?
                p.setPhotoFSPath("wallpaper.png");
                repository.putPhoto(p);

                // Notify User
                // Send Notification
                notifyUser(photo, image);

                // If user wants auto-magical setting
                if (PrefUtils.shouldSetWallpaperAutomatically(context)) {
                    // new SetWallpaperTask(context, false).execute(wallpaper);
                }

                // Stop self
                stopSelf();
            }
        });
    }

    private void notifyUser(Photo photo, Bitmap wallpaper) {
        NotificationUtils.pushNewWallpaperNotif(context, photo, wallpaper);
    }
}
