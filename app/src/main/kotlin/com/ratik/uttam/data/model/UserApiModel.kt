package com.ratik.uttam.data.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Ratik on 17/10/17.
 */
data class UserApiModel(
    @SerializedName("name")
    val name: String,

    @SerializedName("username")
    val username: String,

    @SerializedName("links")
    val links: LinksApiModel
)
