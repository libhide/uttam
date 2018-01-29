package com.ratik.uttam.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Ratik on 17/10/17.
 */

public class LinksResponse {

    @SerializedName("html")
    private String htmlLink;

    @SerializedName("download_location")
    private String downloadLink;

    public String getHtmlLink() {
        return htmlLink;
    }

    public String getDownloadLink() {
        return downloadLink;
    }
}
