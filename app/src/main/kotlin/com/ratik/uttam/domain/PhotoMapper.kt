package com.ratik.uttam.domain

import com.ratik.uttam.data.model.PhotoApiModel
import com.ratik.uttam.domain.model.Photo
import com.ratik.uttam.domain.model.Photographer
import javax.inject.Inject

class PhotoMapper @Inject constructor() {

    fun mapPhoto(photoApiModel: PhotoApiModel, localUri: String): Photo {
        return Photo(
            localUri = localUri,
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