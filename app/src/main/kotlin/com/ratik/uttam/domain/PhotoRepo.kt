package com.ratik.uttam.domain

import com.ratik.uttam.BuildConfig
import com.ratik.uttam.core.DispatcherProvider
import com.ratik.uttam.data.ApiConstants
import com.ratik.uttam.data.UnsplashApi
import com.ratik.uttam.data.extensions.asResult
import com.ratik.uttam.data.whenError
import com.ratik.uttam.data.whenSuccess
import com.ratik.uttam.domain.model.Photo
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

internal class PhotoRepo @Inject constructor(
    private val unsplashApi: UnsplashApi,
    private val dispatcherProvider: DispatcherProvider,
) {
    suspend fun getRandomPhoto() {
        withContext(dispatcherProvider.io) {
            unsplashApi.getRandomPhotoSus(
                clientId = BuildConfig.CLIENT_ID,
                collections = ApiConstants.DEFAULT_COLLECTIONS,
            ).asResult().whenSuccess { photoApiModel ->
                Timber.d("Photo fetched: ${photoApiModel.urls.fullUrl}")
            }.whenError { error ->
                Timber.e("Error fetching photo: $error")
            }
        }
    }
}