package com.ratik.uttam.domain.model

/**
 * Created by Ratik on 17/10/17.
 */
data class Photo(
    val id: String,
    var fullPhotoUri: String?,
    var regularPhotoUri: String?,
    var thumbPhotoUri: String?,
    val photographerName: String,
    val photographerUserName: String,
    val photoFullUrl: String,
    val photoDownloadUrl: String,
    val photoDownloadEndpoint: String?,
    val photoHtmlUrl: String,
)
