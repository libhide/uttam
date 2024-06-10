package com.ratik.uttam.domain

import com.ratik.uttam.data.model.PhotoApiModel
import com.ratik.uttam.domain.model.Photo
import com.ratik.uttam.domain.model.Photographer
import javax.inject.Inject

class PhotoMapper @Inject constructor() {

  fun mapPhoto(
    photoApiModel: PhotoApiModel,
    rawPhotoUri: String,
    regularPhotoUri: String,
    thumbPhotoUri: String,
  ): Photo {
    return Photo(
      id = photoApiModel.id,
      rawPhotoUri = rawPhotoUri,
      regularPhotoUri = regularPhotoUri,
      thumbPhotoUri = thumbPhotoUri,
      shareUrl = photoApiModel.links.htmlLink,
      photographer = mapPhotographer(photoApiModel),
    )
  }

  private fun mapPhotographer(photoApiModel: PhotoApiModel): Photographer {
    return Photographer(
      name = photoApiModel.photographer.name,
      username = photoApiModel.photographer.username,
      profileUrl = photoApiModel.photographer.links.htmlLink,
    )
  }
}
