package com.ratik.uttam.model;

import android.graphics.Bitmap;

/**
 * Created by Ratik on 17/10/17.
 */

public class Photo {
    // Wallpaper
    private Bitmap photo;

    // Meta data
    private String photographerName;
    private String photographerUserName;
    private String photoFullUrl;
    private String photoDownloadUrl;
    private String photoHtmlUrl;

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public String getPhotographerName() {
        return photographerName;
    }

    public void setPhotographerName(String photographerName) {
        this.photographerName = photographerName;
    }

    public String getPhotographerUserName() {
        return photographerUserName;
    }

    public void setPhotographerUserName(String photographerUserName) {
        this.photographerUserName = photographerUserName;
    }

    public String getPhotoFullUrl() {
        return photoFullUrl;
    }

    public void setPhotoFullUrl(String photoFullUrl) {
        this.photoFullUrl = photoFullUrl;
    }

    public String getPhotoDownloadUrl() {
        return photoDownloadUrl;
    }

    public void setPhotoDownloadUrl(String photoDownloadUrl) {
        this.photoDownloadUrl = photoDownloadUrl;
    }

    public String getPhotoHtmlUrl() {
        return photoHtmlUrl;
    }

    public void setPhotoHtmlUrl(String photoHtmlUrl) {
        this.photoHtmlUrl = photoHtmlUrl;
    }
}
