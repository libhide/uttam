package com.ratik.uttam.domain.model

/**
 * Created by Ratik on 17/10/17.
 */
@Deprecated("Use [Photo] instead")
data class PhotoOld(
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
