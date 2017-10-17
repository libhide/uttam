package com.ratik.uttam.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Ratik on 17/10/17.
 */

public class Urls {

    @SerializedName("full")
    private String fullUrl;

    public String getFullUrl() {
        return fullUrl;
    }

    public void setFullUrl(String fullUrl) {
        this.fullUrl = fullUrl;
    }
}
