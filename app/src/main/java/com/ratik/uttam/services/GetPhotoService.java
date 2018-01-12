package com.ratik.uttam.services;

import android.app.Service;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ratik.uttam.Keys;
import com.ratik.uttam.api.UnsplashService;
import com.ratik.uttam.data.DataStore;
import com.ratik.uttam.di.Injector;
import com.ratik.uttam.model.Photo;
import com.ratik.uttam.model._Photo;
import com.ratik.uttam.utils.BitmapUtils;
import com.ratik.uttam.utils.FetchUtils;
import com.ratik.uttam.utils.NotificationUtils;
import com.ratik.uttam.utils.PrefUtils;
import com.ratik.uttam.utils.Utils;

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
        getRandomPhoto();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void getRandomPhoto() {
        Log.i(TAG, "Getting random photo...");
        service.getRandomPhoto(Keys.CLIENT_ID, FetchUtils.getRandomCategory())
                .enqueue(new Callback<_Photo>() {
                    @Override
                    public void onResponse(Call<_Photo> call, Response<_Photo> response) {
                        if (response.isSuccessful()) {
                            Log.i(TAG, "Photo fetched successfully!");
                            _Photo photo = response.body();
                            savePhoto(photo);
                        }
                    }

                    @Override
                    public void onFailure(Call<_Photo> call, Throwable t) {
                        Log.e(TAG, "onFailure: " + t.getMessage());
                    }
                });
    }

    private void savePhoto(_Photo photo) {
        Observable.fromCallable(() -> {
            URL url = new URL(photo.getUrls().getFullUrl());
            Log.i(TAG, "Url: " + url.toString());
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
        .map(bitmap -> BitmapUtils.scaleBitmap(context, bitmap))
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe((image) -> {
            if (image != null) {
                Photo p = new Photo();
                p.setPhotographerName(Utils.toTitleCase(photo.getPhotographer().getName()));
                p.setPhotographerUserName(photo.getPhotographer().getUsername());
                p.setPhotoDownloadUrl(photo.getLinks().getDownloadLink());
                p.setPhotoHtmlUrl(photo.getLinks().getHtmlLink());
                p.setPhotoFullUrl(photo.getUrls().getFullUrl());
                p.setPhoto(image);

                dataStore.putPhoto(p);

                // Notify User
                notificationUtils.pushNewWallpaperNotif(p);

                // If user wants auto-magical setting, set the wallpaper
                if (PrefUtils.shouldSetWallpaperAutomatically(context)) {
                    WallpaperManager.getInstance(context).setBitmap(image);
                }

                Log.i(TAG, "Photo saved successfully!");

                stopSelf();
            } else {
                Log.i(TAG, "Saving isn't working for some reason.");
            }
        });
    }
}
