package com.ratik.uttam.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Ratik on 17/10/17.
 */

public class UrlsResponse {

    @SerializedName("full")
    private String fullUrl;

    @SerializedName("regular")
    private String regularUrl;

    @SerializedName("thumb")
    private String thumbUrl;

    public String getFullUrl() {
        return fullUrl;
    }

    public String getRegularUrl() {
        return regularUrl;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }
}
