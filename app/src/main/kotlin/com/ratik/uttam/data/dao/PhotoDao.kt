package com.ratik.uttam.data.dao

import android.content.SharedPreferences
import com.ratik.uttam.domain.model.Photo
import com.ratik.uttam.domain.model.Photographer
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
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
    check(editor.commit()) { "Could not persist wallpaper details" }
  }

  fun observePhoto(): Flow<Photo?> = callbackFlow {
    val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
      if (key in PHOTO_KEYS) trySend(getPhoto())
    }
    sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    trySend(getPhoto())
    awaitClose { sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener) }
  }.distinctUntilChanged()

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

  private companion object {
    val PHOTO_KEYS = setOf(
      "id",
      "rawPhotoUri",
      "regularPhotoUri",
      "thumbPhotoUri",
      "shareUrl",
      "photographerName",
      "photographerUsername",
      "photographerProfileUrl",
    )
  }
}
