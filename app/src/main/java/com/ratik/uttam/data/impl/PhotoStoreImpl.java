package com.ratik.uttam.data.impl;

import android.content.SharedPreferences;
import android.util.Log;

import com.ratik.uttam.Constants;
import com.ratik.uttam.data.PhotoStore;
import com.ratik.uttam.domain.model.PhotoOld;

import io.reactivex.Completable;
import io.reactivex.Single;

public class PhotoStoreImpl implements PhotoStore {

    private SharedPreferences prefs;

    public PhotoStoreImpl(SharedPreferences prefs) {
        this.prefs = prefs;
    }

    @Override
    public Completable putPhoto(PhotoOld photo) {
        return Completable.fromAction(() -> storePhoto(photo));
    }

    private void storePhoto(PhotoOld photo) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.Data.PHOTO_ID, photo.getId());
        editor.putString(Constants.Data.PHOTO_URI, photo.getFullPhotoUri());
        editor.putString(Constants.Data.PHOTO_REGULAR_URI, photo.getRegularPhotoUri());
        editor.putString(Constants.Data.PHOTO_THUMB_URI, photo.getThumbPhotoUri());
        editor.putString(Constants.Data.PHOTOGRAPHER_NAME, photo.getPhotographerName());
        editor.putString(Constants.Data.PHOTOGRAPHER_USERNAME, photo.getPhotographerUserName());
        editor.putString(Constants.Data.FULL_URL, photo.getPhotoFullUrl());
        editor.putString(Constants.Data.DOWNLOAD_URL, photo.getPhotoDownloadUrl());
        editor.putString(Constants.Data.DOWNLOAD_ENDPOINT, photo.getPhotoDownloadEndpoint());
        editor.putString(Constants.Data.HTML_URL, photo.getPhotoHtmlUrl());
        editor.apply();

        Log.d(PhotoStore.class.getSimpleName(), "Stored to prefs");
    }

    @Override
    public Single<PhotoOld> getPhoto() {
        PhotoOld photo = new PhotoOld(
                prefs.getString(Constants.Data.PHOTO_ID, ""),
                prefs.getString(Constants.Data.PHOTO_URI, ""),
                prefs.getString(Constants.Data.PHOTO_REGULAR_URI, ""),
                prefs.getString(Constants.Data.PHOTO_THUMB_URI, ""),
                prefs.getString(Constants.Data.PHOTOGRAPHER_NAME, ""),
                prefs.getString(Constants.Data.PHOTOGRAPHER_USERNAME, ""),
                prefs.getString(Constants.Data.FULL_URL, ""),
                prefs.getString(Constants.Data.DOWNLOAD_URL, ""),
                prefs.getString(Constants.Data.DOWNLOAD_ENDPOINT, ""),
                prefs.getString(Constants.Data.HTML_URL, "")
        );

        return Single.fromCallable(() -> photo);
    }
}
