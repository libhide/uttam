package com.ratik.uttam.data.impl;

import android.content.SharedPreferences;
import android.util.Log;

import com.ratik.uttam.Constants;
import com.ratik.uttam.data.DataStore;
import com.ratik.uttam.di.Injector;
import com.ratik.uttam.model.Photo;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Single;


/**
 * Created by Ratik on 09/01/18.
 */

public class DataStoreImpl implements DataStore {

    @Inject
    SharedPreferences prefs;

    public DataStoreImpl() {
        Injector.getAppComponent().inject(this);
    }

    @Override
    public Completable putPhoto(Photo photo) {
        return Completable.fromAction(() -> storePhoto(photo));
    }

    private void storePhoto(Photo photo) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.Data.PHOTO_ID, photo.getId());
        editor.putString(Constants.Data.PHOTO_URI, photo.getPhotoUri());
        editor.putString(Constants.Data.PHOTO_REGULAR_URI, photo.getRegularPhotoUri());
        editor.putString(Constants.Data.PHOTO_THUMB_URI, photo.getThumbPhotoUri());
        editor.putString(Constants.Data.PHOTOGRAPHER_NAME, photo.getPhotographerName());
        editor.putString(Constants.Data.PHOTOGRAPHER_USERNAME, photo.getPhotographerUserName());
        editor.putString(Constants.Data.FULL_URL, photo.getPhotoFullUrl());
        editor.putString(Constants.Data.DOWNLOAD_URL, photo.getPhotoDownloadUrl());
        editor.putString(Constants.Data.HTML_URL, photo.getPhotoHtmlUrl());
        editor.apply();
        Log.d(DataStore.class.getSimpleName(), "Stored to prefs");
    }

    @Override
    public Single<Photo> getPhoto() {
        return Single.fromCallable(() -> new Photo.Builder()
                .setId(prefs.getString(Constants.Data.PHOTO_ID, ""))
                .setPhotoUri(prefs.getString(Constants.Data.PHOTO_URI, ""))
                .setRegularPhotoUri(prefs.getString(Constants.Data.PHOTO_REGULAR_URI, ""))
                .setThumbPhotoUri(prefs.getString(Constants.Data.PHOTO_THUMB_URI, ""))
                .setPhotographerName(prefs.getString(Constants.Data.PHOTOGRAPHER_NAME, ""))
                .setPhotographerUserName(prefs.getString(Constants.Data.PHOTOGRAPHER_USERNAME, ""))
                .setPhotoFullUrl(prefs.getString(Constants.Data.FULL_URL, ""))
                .setPhotoDownloadUrl(prefs.getString(Constants.Data.DOWNLOAD_URL, ""))
                .setPhotoHtmlUrl(prefs.getString(Constants.Data.HTML_URL, ""))
                .build());
    }
}
