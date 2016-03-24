package com.ratik.uttam.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ratik on 26/02/16.
 */
public class Photo implements Parcelable {
    private String urlFull;
    private String urlRegular;
    private String photographer;
    private String htmlUrl;
    private String downloadUrl;
    private String color;

    public Photo() {
        // Default constructor
    }

    public String getUrlFull() {
        return urlFull;
    }

    public void setUrlFull(String urlFull) {
        this.urlFull = urlFull;
    }

    public String getUrlRegular() {
        return urlRegular;
    }

    public void setUrlRegular(String urlRegular) {
        this.urlRegular = urlRegular;
    }

    public String getPhotographer() {
        return photographer;
    }

    public void setPhotographer(String photographer) {
        this.photographer = photographer;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.urlFull);
        dest.writeString(this.urlRegular);
        dest.writeString(this.photographer);
        dest.writeString(this.htmlUrl);
        dest.writeString(this.downloadUrl);
        dest.writeString(this.color);
    }

    protected Photo(Parcel in) {
        this.urlFull = in.readString();
        this.urlRegular = in.readString();
        this.photographer = in.readString();
        this.downloadUrl = in.readString();
        this.htmlUrl = in.readString();
        this.color = in.readString();
    }

    public static final Parcelable.Creator<Photo> CREATOR = new Parcelable.Creator<Photo>() {
        public Photo createFromParcel(Parcel source) {
            return new Photo(source);
        }

        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };
}
