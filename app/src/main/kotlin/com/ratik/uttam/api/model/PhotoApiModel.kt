package com.ratik.uttam.api.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Ratik on 26/02/16.
 */
data class PhotoApiModel(
    @SerializedName("id")
    val id: String,

    @SerializedName("urls")
    val urls: UrlsApiModel,

    @SerializedName("links")
    val links: LinksApiModel,

    @SerializedName("user")
    val photographer: UserApiModel,
)
