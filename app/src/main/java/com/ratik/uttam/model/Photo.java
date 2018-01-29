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

    private Photo(Bitmap photo, String photographerName, String photographerUserName,
                 String photoFullUrl, String photoDownloadUrl, String photoHtmlUrl) {
        this.photo = photo;
        this.photographerName = photographerName;
        this.photographerUserName = photographerUserName;
        this.photoFullUrl = photoFullUrl;
        this.photoDownloadUrl = photoDownloadUrl;
        this.photoHtmlUrl = photoHtmlUrl;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public String getPhotographerName() {
        return photographerName;
    }

    public String getPhotographerUserName() {
        return photographerUserName;
    }

    public String getPhotoFullUrl() {
        return photoFullUrl;
    }

    public String getPhotoDownloadUrl() {
        return photoDownloadUrl;
    }

    public String getPhotoHtmlUrl() {
        return photoHtmlUrl;
    }

    public static class Builder {
        // Wallpaper
        private Bitmap photo;

        // Meta data
        private String photographerName;
        private String photographerUserName;
        private String photoFullUrl;
        private String photoDownloadUrl;
        private String photoHtmlUrl;

        public Builder setPhoto(Bitmap bitmap) {
            this.photo = bitmap;
            return this;
        }

        public Builder setPhotographerName(String photographerName) {
            this.photographerName = photographerName;
            return this;
        }

        public Builder setPhotographerUserName(String photographerUserName) {
            this.photographerUserName = photographerUserName;
            return this;
        }

        public Builder setPhotoFullUrl(String photoFullUrl) {
            this.photoFullUrl = photoFullUrl;
            return this;
        }

        public Builder setPhotoDownloadUrl(String photoDownloadUrl) {
            this.photoDownloadUrl = photoDownloadUrl;
            return this;
        }

        public Builder setPhotoHtmlUrl(String photoHtmlUrl) {
            this.photoHtmlUrl = photoHtmlUrl;
            return this;
        }

        public Photo build() {
            return new Photo(photo, photographerName, photographerUserName,
                    photoFullUrl, photoDownloadUrl, photoHtmlUrl);
        }
    }
}
