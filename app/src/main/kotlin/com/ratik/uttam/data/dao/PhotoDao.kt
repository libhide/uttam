package com.ratik.uttam.data.dao

import android.content.SharedPreferences
import com.ratik.uttam.domain.model.Photo
import com.ratik.uttam.domain.model.Photographer
import javax.inject.Inject

// TODO: Clean up this class after Kotlin port is complete
class PhotoDao @Inject constructor(private val sharedPreferences: SharedPreferences) {

  fun savePhoto(photo: Photo) {
    val editor = sharedPreferences.edit()
    editor.putString("id", photo.id)
    editor.putString("rawPhotoUri", photo.rawPhotoUri)
    editor.putString("regularPhotoUri", photo.regularPhotoUri)
    editor.putString("thumbPhotoUri", photo.thumbPhotoUri)
    editor.putString("shareUrl", photo.shareUrl)
    editor.putString("photographerName", photo.photographer.name)
    editor.putString("photographerUsername", photo.photographer.username)
    editor.putString("photographerProfileUrl", photo.photographer.profileUrl)
    editor.apply()
  }

  fun getPhoto(): Photo? {
    val id = sharedPreferences.getString("id", "")
    val rawPhotoUri = sharedPreferences.getString("rawPhotoUri", "")
    val regularPhotoUri = sharedPreferences.getString("regularPhotoUri", "")
    val thumbPhotoUri = sharedPreferences.getString("thumbPhotoUri", "")
    val shareUrl = sharedPreferences.getString("shareUrl", "")
    val photographerName = sharedPreferences.getString("photographerName", "")
    val photographerUsername = sharedPreferences.getString("photographerUsername", "")
    val photographerProfileUrl = sharedPreferences.getString("photographerProfileUrl", "")

    return if (rawPhotoUri == "" || regularPhotoUri == "" || thumbPhotoUri == "" || shareUrl == "" || photographerName == "" || photographerUsername == "" || photographerProfileUrl == "") {
      null
    } else {
      Photo(
        id = id!!,
        rawPhotoUri = rawPhotoUri!!,
        regularPhotoUri = regularPhotoUri!!,
        thumbPhotoUri = thumbPhotoUri!!,
        photographer = Photographer(
          name = photographerName!!,
          username = photographerUsername!!,
          profileUrl = photographerProfileUrl!!,
        ),
        shareUrl = shareUrl!!,
      )
    }
  }
}
