package com.ratik.uttam.model;

import io.realm.RealmObject;

/**
 * Created by Ratik on 17/10/17.
 */

public class _Photo extends RealmObject {
    private String photoFileName;
    private String photographerName;
    private String photographerUserName;
    private String photoFullUrl;
    private String photoDownloadUrl;
    private String photoHtmlUrl;

    public String getPhotoFileName() {
        return photoFileName;
    }

    public void setPhotoFileName(String photoFileName) {
        this.photoFileName = photoFileName;
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