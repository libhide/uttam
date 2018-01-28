package com.ratik.uttam.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.ratik.uttam.BuildConfig;
import com.ratik.uttam.api.UnsplashService;
import com.ratik.uttam.data.DataStore;
import com.ratik.uttam.di.Injector;
import com.ratik.uttam.model.Photo;
import com.ratik.uttam.model._Photo;
import com.ratik.uttam.utils.FetchUtils;
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
 * Created by Ratik on 23/10/17.
 */

public class GetPhotoJob extends JobService {
    private static final String TAG = GetPhotoJob.class.getSimpleName();

    @Inject
    UnsplashService service;

    @Inject
    DataStore dataStore;

    @Inject
    NotificationUtils notificationUtils;

    private Context context;

    public GetPhotoJob() {
        context = GetPhotoJob.this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Injector.getAppComponent().inject(this);
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        fetchPhoto(jobParameters);
        return true;
    }

    private void fetchPhoto(JobParameters parameters) {
        Log.i(TAG, "Fetching photo...");
        service.getRandomPhoto2(BuildConfig.CLIENT_ID, FetchUtils.getRandomCategory())
                .map(photoResponse -> photoResponse.body())
                .map(photo -> makePhotoObject(photo))
                .map(photo -> dataStore.putPhoto(photo))
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        (completable) -> {
                            // Notify User
                            // notificationUtils.pushNewWallpaperNotif(p);

                            // If user wants auto-magical setting, set the wallpaper
                            if (PrefUtils.shouldSetWallpaperAutomatically(context)) {
                                // WallpaperManager.getInstance(context).setBitmap(image);
                            }

                            Log.i(TAG, "Photo saved successfully!");

                            // job is done
                            jobFinished(parameters, false);
                        },
                        (throwable -> {
                            Log.e(TAG, throwable.getMessage());
                        })
                );
    }

    private Bitmap downloadWallpaperImage(_Photo photo) throws IOException {
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

    private Photo makePhotoObject(_Photo photo) throws IOException {
        Bitmap wallpaperImage = downloadWallpaperImage(photo);
        if (wallpaperImage != null) {
            // todo: implement builder pattern for Photo.class
            Photo p = new Photo();
            p.setPhotographerName(Utils.toTitleCase(photo.getPhotographer().getName()));
            p.setPhotographerUserName(photo.getPhotographer().getUsername());
            p.setPhotoDownloadUrl(photo.getLinks().getDownloadLink());
            p.setPhotoHtmlUrl(photo.getLinks().getHtmlLink());
            p.setPhotoFullUrl(photo.getUrls().getFullUrl());
            p.setPhoto(wallpaperImage);
            return p;
        } else {
            // todo: do something here
            return null;
        }
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true;
    }
}
