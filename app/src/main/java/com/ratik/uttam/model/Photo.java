package com.ratik.uttam.model;

/**
 * Created by Ratik on 17/10/17.
 */

public class Photo {
    // Wallpaper
    private String fullPhotoUri;
    private String regularPhotoUri;
    private String thumbPhotoUri;

    // Meta data
    private String id;
    private String photographerName;
    private String photographerUserName;
    private String photoFullUrl;
    private String photoDownloadUrl;
    private String photoHtmlUrl;

    private Photo(String id, String fullPhotoUri, String regularPhotoUri, String thumbPhotoUri,
                  String photographerName, String photographerUserName, String photoFullUrl,
                  String photoDownloadUrl, String photoHtmlUrl) {
        this.id = id;
        this.fullPhotoUri = fullPhotoUri;
        this.regularPhotoUri = regularPhotoUri;
        this.thumbPhotoUri = thumbPhotoUri;
        this.photographerName = photographerName;
        this.photographerUserName = photographerUserName;
        this.photoFullUrl = photoFullUrl;
        this.photoDownloadUrl = photoDownloadUrl;
        this.photoHtmlUrl = photoHtmlUrl;
    }

    public String getId() {
        return id;
    }

    public String getFullPhotoUri() {
        return fullPhotoUri;
    }

    public void setFullPhotoUri(String fullPhotoUri) {
        this.fullPhotoUri = fullPhotoUri;
    }

    public String getRegularPhotoUri() {
        return regularPhotoUri;
    }

    public void setRegularPhotoUri(String regularPhotoUri) {
        this.regularPhotoUri = regularPhotoUri;
    }

    public String getThumbPhotoUri() {
        return thumbPhotoUri;
    }

    public void setThumbPhotoUri(String thumbPhotoUri) {
        this.thumbPhotoUri = thumbPhotoUri;
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
        private String photoUri;
        private String regularPhotoUri;
        private String thumbPhotoUri;

        // Meta data
        private String id;
        private String photographerName;
        private String photographerUserName;
        private String photoFullUrl;
        private String photoDownloadUrl;
        private String photoHtmlUrl;

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setPhotoUri(String photoUri) {
            this.photoUri = photoUri;
            return this;
        }

        public Builder setRegularPhotoUri(String regularPhotoUri) {
            this.regularPhotoUri = regularPhotoUri;
            return this;
        }

        public Builder setThumbPhotoUri(String thumbPhotoUri) {
            this.thumbPhotoUri = thumbPhotoUri;
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
            return new Photo(id, photoUri, regularPhotoUri, thumbPhotoUri, photographerName,
                    photographerUserName, photoFullUrl, photoDownloadUrl, photoHtmlUrl);
        }
    }
}
