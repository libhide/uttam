package com.ratik.uttam.data.dao

import android.content.SharedPreferences
import com.ratik.uttam.domain.model.Photo
import com.ratik.uttam.domain.model.Photographer
import javax.inject.Inject

class PhotoDao @Inject constructor(private val sharedPreferences: SharedPreferences) {

  fun savePhoto(photo: Photo) {
    val editor = sharedPreferences.edit()
    editor.putString("id", photo.id)
    editor.putString("localUri", photo.localUri)
    editor.putString("shareUrl", photo.shareUrl)
    editor.putString("photographerName", photo.photographer.name)
    editor.putString("photographerUsername", photo.photographer.username)
    editor.putString("photographerProfileUrl", photo.photographer.profileUrl)
    editor.apply()
  }

  fun getPhoto(): Photo? {
    val id = sharedPreferences.getString("id", "")
    val localUri = sharedPreferences.getString("localUri", "")
    val shareUrl = sharedPreferences.getString("shareUrl", "")
    val photographerName = sharedPreferences.getString("photographerName", "")
    val photographerUsername = sharedPreferences.getString("photographerUsername", "")
    val photographerProfileUrl = sharedPreferences.getString("photographerProfileUrl", "")

    return if (
      localUri == "" ||
        shareUrl == "" ||
        photographerName == "" ||
        photographerUsername == "" ||
        photographerProfileUrl == ""
    ) {
      null
    } else {
      Photo(
        id = id!!,
        localUri = localUri!!,
        photographer =
          Photographer(
            name = photographerName!!,
            username = photographerUsername!!,
            profileUrl = photographerProfileUrl!!,
          ),
        shareUrl = shareUrl!!,
      )
    }
  }
}
