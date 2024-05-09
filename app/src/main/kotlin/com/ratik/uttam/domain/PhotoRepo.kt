package com.ratik.uttam.domain

import com.ratik.uttam.BuildConfig
import com.ratik.uttam.core.DispatcherProvider
import com.ratik.uttam.data.ApiConstants
import com.ratik.uttam.data.UnsplashApi
import com.ratik.uttam.data.dao.PhotoDao
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
    private val photoDao: PhotoDao,
    private val mapper: PhotoMapper,
) {
    suspend fun fetchRandomPhoto(): Flow<Photo> = flow {
        unsplashApi.getRandomPhotoSus3(
            clientId = BuildConfig.CLIENT_ID,
            collections = ApiConstants.DEFAULT_COLLECTIONS,
        ).asResult().whenSuccess { photoApiModel ->
            // double the height and get a square image to ensure a scrolling wallpaper
            val wallpaperUrl = photoApiModel.urls.rawUrl + "&w=4276&h=4276&fit=crop"
            val localUri = wallpaperDownloader.downloadWallpaper(
                photoApiModel.id,
                wallpaperUrl,
            )
            if (localUri != null) {
                val photo = mapper.mapPhoto(photoApiModel, localUri)
                photoDao.savePhoto(photo)
                emit(photo)
            } else {
                throw WallpaperrDownloadFailedException()
            }
        }.whenError { error ->
            Timber.e("Error fetching: $error")
            throw error
        }
    }.flowOn(dispatcherProvider.io)

    suspend fun getCurrentWallpaper(): Flow<Photo> = flow {
        val photo = photoDao.getPhoto()
        if (photo != null) {
            emit(photo)
        } else {
            // Ideally, this situation won't arise as a wallpaper would always be saved
            // as part of onboarding.
            // TODO: re-work this logic once onboarding is implemented
            throw WallpaperrDownloadFailedException()
        }
    }.flowOn(dispatcherProvider.io)
}