package com.ratik.uttam.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Ratik on 26/02/16.
 */
public class _Photo {

    @SerializedName("urls")
    private _Urls urls;

    @SerializedName("links")
    private _Links links;

    @SerializedName("user")
    private _Photographer photographer;

    public _Urls getUrls() {
        return urls;
    }

    public _Links getLinks() {
        return links;
    }

    public _Photographer getPhotographer() {
        return photographer;
    }
}
