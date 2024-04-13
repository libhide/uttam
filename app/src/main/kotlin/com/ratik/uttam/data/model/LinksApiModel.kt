package com.ratik.uttam.data.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Ratik on 17/10/17.
 */
data class LinksApiModel(
    @SerializedName("html")
    val htmlLink: String,

    @SerializedName("download")
    val downloadLink: String,

    @SerializedName("download_location")
    val downloadEndpoint: String,
)
