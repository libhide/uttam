package com.ratik.uttam.data.impl;

import android.content.SharedPreferences;
import android.graphics.Bitmap;

import com.ratik.uttam.Constants;
import com.ratik.uttam.data.DataStore;
import com.ratik.uttam.di.Injector;
import com.ratik.uttam.model.Photo;
import com.ratik.uttam.utils.PhotoSaver;

import javax.inject.Inject;

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
    public void putPhoto(Photo photo) {
        photoSaver.save(photo.getPhoto());

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.Data.PHOTOGRAPHER_NAME, photo.getPhotographerName());
        editor.putString(Constants.Data.PHOTOGRAPHER_USERNAME, photo.getPhotographerUserName());
        editor.putString(Constants.Data.FULL_URL, photo.getPhotoFullUrl());
        editor.putString(Constants.Data.DOWNLOAD_URL, photo.getPhotoDownloadUrl());
        editor.putString(Constants.Data.HTML_URL, photo.getPhotoHtmlUrl());
        editor.apply();
    }

    @Override
    public Photo getPhoto() {
        Bitmap wallpaper = photoSaver.load();

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
