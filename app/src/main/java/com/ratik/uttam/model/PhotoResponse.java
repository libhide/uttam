package com.ratik.uttam.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Ratik on 26/02/16.
 */
public class PhotoResponse {

    @SerializedName("id")
    private String id;

    @SerializedName("urls")
    private UrlsResponse urls;

    @SerializedName("links")
    private LinksResponse links;

    @SerializedName("user")
    private PhotographerResponse photographer;

    public String getId() {
        return id;
    }

    public UrlsResponse getUrls() {
        return urls;
    }

    public LinksResponse getLinks() {
        return links;
    }

    public PhotographerResponse getPhotographer() {
        return photographer;
    }
}
