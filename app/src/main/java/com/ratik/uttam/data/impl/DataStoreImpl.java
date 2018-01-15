package com.ratik.uttam.data.impl;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.ratik.uttam.Constants;
import com.ratik.uttam.data.DataStore;
import com.ratik.uttam.di.Injector;
import com.ratik.uttam.model.Photo;
import com.ratik.uttam.utils.PhotoSaver;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Single;


/**
 * Created by Ratik on 09/01/18.
 */

public class DataStoreImpl implements DataStore {

    @Inject
    SharedPreferences prefs;

    @Inject
    PhotoSaver photoSaver;

    public DataStoreImpl() {
        Injector.getAppComponent().inject(this);
    }

    @Override
    public Completable putPhoto(Photo photo) {
        photoSaver.setExternal(false)
                .setFileName(Constants.General.WALLPAPER_FILE_NAME)
                .save(photo.getPhoto());
        storePhotoMetadata(photo);
        return Completable.complete();
    }

    private void storePhotoMetadata(Photo photo) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.Data.PHOTOGRAPHER_NAME, photo.getPhotographerName());
        editor.putString(Constants.Data.PHOTOGRAPHER_USERNAME, photo.getPhotographerUserName());
        editor.putString(Constants.Data.FULL_URL, photo.getPhotoFullUrl());
        editor.putString(Constants.Data.DOWNLOAD_URL, photo.getPhotoDownloadUrl());
        editor.putString(Constants.Data.HTML_URL, photo.getPhotoHtmlUrl());
        editor.apply();
    }

    @Override
    public Single<Photo> getPhoto() {
        return Single.fromCallable(() -> {
            Bitmap wallpaper = photoSaver.load();
            return getPhoto(wallpaper);
        });
    }

    @NonNull
    private Photo getPhoto(Bitmap wallpaper) {
        Photo photo = new Photo();
        photo.setPhoto(wallpaper);
        photo.setPhotographerName(prefs.getString(Constants.Data.PHOTOGRAPHER_NAME, ""));
        photo.setPhotographerUserName(prefs.getString(Constants.Data.PHOTOGRAPHER_USERNAME, ""));
        photo.setPhotoFullUrl(prefs.getString(Constants.Data.FULL_URL, ""));
        photo.setPhotoDownloadUrl(prefs.getString(Constants.Data.DOWNLOAD_URL, ""));
        photo.setPhotoHtmlUrl(prefs.getString(Constants.Data.HTML_URL, ""));
        return photo;
    }

    @Override
    public void clear() {
        // unimplemented
    }
}
