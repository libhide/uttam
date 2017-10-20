package com.ratik.uttam.services;

import android.app.Service;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ratik.uttam.Constants;
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
        repository.clear();
        Log.i(TAG, "Getting random photo...");
        service.getRandomPhoto(Keys.CLIENT_ID, FetchUtils.getRandomCategory())
                .enqueue(new Callback<Photo>() {
                    @Override
                    public void onResponse(Call<Photo> call, Response<Photo> response) {
                        if (response.isSuccessful()) {
                            Log.i(TAG, "Photo fetched successfully!");
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
                FileUtils.saveBitmapToInternalStorage(context, image, Constants.General.WALLPAPER_FILE_NAME);

                // Save photo data to Realm
                _Photo p = new _Photo();
                p.setPhotographerName(photo.getPhotographer().getName());
                p.setPhotographerUserName(photo.getPhotographer().getUsername());
                p.setPhotoDownloadUrl(photo.getLinks().getDownloadLink());
                p.setPhotoHtmlUrl(photo.getLinks().getHtmlLink());
                p.setPhotoFullUrl(photo.getUrls().getFullUrl());
                p.setPhotoFileName(Constants.General.WALLPAPER_FILE_NAME);
                repository.putPhoto(p);

                // Notify User
                NotificationUtils.pushNewWallpaperNotif(context, p);

                // If user wants auto-magical setting, set the wallpaper
                if (PrefUtils.shouldSetWallpaperAutomatically(context)) {
                    WallpaperManager.getInstance(context).setBitmap(image);
                }

                Log.i(TAG, "Photo saved successfully!");

                // Stop self
                stopSelf();
            }
        });
    }
}
