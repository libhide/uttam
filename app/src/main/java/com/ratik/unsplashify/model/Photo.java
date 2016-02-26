package com.ratik.unsplashify.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ratik on 26/02/16.
 */
public class Photo implements Parcelable {
    private int width;
    private int height;
    private String color;
    private String urlFull;
    private String urlRegular;
    private String urlSmall;
    private String urlThumb;
    private String user;
    private String name;

    public Photo() {
        // Default constructor
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
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

    public String getUrlSmall() {
        return urlSmall;
    }

    public void setUrlSmall(String urlSmall) {
        this.urlSmall = urlSmall;
    }

    public String getUrlThumb() {
        return urlThumb;
    }

    public void setUrlThumb(String urlThumb) {
        this.urlThumb = urlThumb;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeString(this.color);
        dest.writeString(this.urlFull);
        dest.writeString(this.urlRegular);
        dest.writeString(this.urlSmall);
        dest.writeString(this.urlThumb);
        dest.writeString(this.user);
        dest.writeString(this.name);
    }

    protected Photo(Parcel in) {
        this.width = in.readInt();
        this.height = in.readInt();
        this.color = in.readString();
        this.urlFull = in.readString();
        this.urlRegular = in.readString();
        this.urlSmall = in.readString();
        this.urlThumb = in.readString();
        this.user = in.readString();
        this.name = in.readString();
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
