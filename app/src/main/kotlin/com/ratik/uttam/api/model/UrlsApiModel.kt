package com.ratik.uttam.api.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Ratik on 17/10/17.
 */
data class UrlsApiModel(
    @SerializedName("full")
    val fullUrl: String,

    @SerializedName("regular")
    val regularUrl: String,

    @SerializedName("thumb")
    val thumbUrl: String,
)
