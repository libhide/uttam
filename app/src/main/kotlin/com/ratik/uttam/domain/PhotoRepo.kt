package com.ratik.uttam.domain

import com.ratik.uttam.BuildConfig
import com.ratik.uttam.core.DispatcherProvider
import com.ratik.uttam.data.ApiConstants
import com.ratik.uttam.data.UnsplashApi
import com.ratik.uttam.data.extensions.asResult
import com.ratik.uttam.data.storage.WallpaperDownloader
import com.ratik.uttam.data.whenError
import com.ratik.uttam.data.whenSuccess
import com.ratik.uttam.domain.exceptions.WallpaperrDownloadFailedException
import com.ratik.uttam.domain.model.Photo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject

internal class PhotoRepo @Inject constructor(
    private val unsplashApi: UnsplashApi,
    private val dispatcherProvider: DispatcherProvider,
    private val wallpaperDownloader: WallpaperDownloader,
    private val mapper: PhotoMapper,
) {
    suspend fun fetchRandomPhoto(): Flow<Photo> = flow {
        unsplashApi.getRandomPhotoSus(
            clientId = BuildConfig.CLIENT_ID,
            collections = ApiConstants.DEFAULT_COLLECTIONS,
        ).asResult()
            .whenSuccess { photoApiModel ->
                // double the height and get a square image to ensure a scrolling wallpaper
                val wallpaperUrl =
                    photoApiModel.urls.rawUrl + "&w=4276&h=4276&fit=crop"
                val localUri = wallpaperDownloader.downloadWallpaper(
                    photoApiModel.id,
                    wallpaperUrl,
                )
                if (localUri != null) {
                    emit(mapper.mapPhoto(photoApiModel, localUri))
                } else {
                    throw WallpaperrDownloadFailedException()
                }
            }
            .whenError { error ->
                Timber.e("Error fetching photo: $error")
                throw error
            }
    }.flowOn(dispatcherProvider.io)
}