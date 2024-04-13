package com.ratik.uttam.api.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Ratik on 17/10/17.
 */
data class UserApiModel(
    @SerializedName("name")
    val name: String? = null,

    @SerializedName("username")
    val username: String? = null
)
