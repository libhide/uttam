package com.ratik.uttam.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Ratik on 26/02/16.
 */
public class Photo {

    @SerializedName("urls")
    private Urls urls;

    @SerializedName("links")
    private Links links;

    @SerializedName("user")
    private Photographer photographer;

    public Urls getUrls() {
        return urls;
    }

    public void setUrls(Urls urls) {
        this.urls = urls;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public Photographer getPhotographer() {
        return photographer;
    }

    public void setPhotographer(Photographer photographer) {
        this.photographer = photographer;
    }
}
