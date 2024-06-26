package com.ratik.uttam.data.model

import com.google.gson.annotations.SerializedName

/** Created by Ratik on 17/10/17. */
data class UrlsApiModel(
  @SerializedName("raw") val rawUrl: String,
  @SerializedName("full") val fullUrl: String,
  @SerializedName("regular") val regularUrl: String,
  @SerializedName("thumb") val thumbUrl: String,
)
