package com.ratik.uttam.domain

import com.ratik.uttam.BuildConfig
import com.ratik.uttam.core.DispatcherProvider
import com.ratik.uttam.data.ApiConstants
import com.ratik.uttam.data.UnsplashApi
import com.ratik.uttam.data.dao.PhotoDao
import com.ratik.uttam.data.dao.UserDao
import com.ratik.uttam.data.extensions.asResult
import com.ratik.uttam.data.model.PhotoApiModel
import com.ratik.uttam.data.storage.WallpaperDownloader
import com.ratik.uttam.data.whenError
import com.ratik.uttam.data.whenSuccess
import com.ratik.uttam.domain.exceptions.PhotoNotFoundException
import com.ratik.uttam.domain.exceptions.WallpaperDownloadFailedException
import com.ratik.uttam.domain.model.Photo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject

internal class PhotoRepo @Inject constructor(
  private val unsplashApi: UnsplashApi,
  private val dispatcherProvider: DispatcherProvider,
  private val wallpaperDownloader: WallpaperDownloader,
  private val photoDao: PhotoDao,
  private val userDao: UserDao,
  private val mapper: PhotoMapper,
) {

  suspend fun fetchDefaultPhoto(): Flow<Photo> = flow {
    val deviceHeight = userDao.getDeviceHeight()
    val deviceWidth = userDao.getDeviceWidth()

    unsplashApi
      .getPhoto(
        clientId = BuildConfig.CLIENT_ID,
        photoId = ApiConstants.DEFAULT_WALLPAPER_ID,
        w = deviceWidth,
        h = deviceHeight,
      )
      .asResult()
      .whenSuccess { photoApiModel ->
        savePhoto(
          deviceHeight = deviceHeight,
          photoApiModel = photoApiModel,
          flowCollector = this,
        )
      }
      .whenError { error ->
        Timber.e("Error fetching default photo: $error")
        throw error
      }
  }.flowOn(dispatcherProvider.io)

  suspend fun fetchRandomPhoto(): Flow<Photo> = flow {
    val deviceHeight = userDao.getDeviceHeight()
    val deviceWidth = userDao.getDeviceWidth()

    unsplashApi
      .getRandomPhotoSus(
        clientId = BuildConfig.CLIENT_ID,
        collections = ApiConstants.DEFAULT_COLLECTIONS,
        w = deviceWidth,
        h = deviceHeight,
      )
      .asResult()
      .whenSuccess { photoApiModel ->
        savePhoto(
          deviceHeight = deviceHeight,
          photoApiModel = photoApiModel,
          flowCollector = this,
        )
      }
      .whenError { error ->
        Timber.e("Error fetching photo: $error")
        throw error
      }
  }.flowOn(dispatcherProvider.io)

  private suspend fun savePhoto(
    deviceHeight: Int,
    photoApiModel: PhotoApiModel,
    flowCollector: FlowCollector<Photo>,
  ) {
    wallpaperDownloader.clearCacheFolder()
    val rawPhotoUri = downloadRawImage(deviceHeight, photoApiModel)
    val regularPhotoUri = downloadRegularImage(photoApiModel)
    val thumbPhotoUri = downloadThumbImage(photoApiModel)
    unsplashApi.incrementDownloadCount(
      url = photoApiModel.links.downloadEndpoint,
      clientId = BuildConfig.CLIENT_ID,
    )
    if (rawPhotoUri != null && regularPhotoUri != null && thumbPhotoUri != null) {
      val photo = mapper.mapPhoto(
        photoApiModel = photoApiModel,
        rawPhotoUri = rawPhotoUri,
        regularPhotoUri = regularPhotoUri,
        thumbPhotoUri = thumbPhotoUri,
      )
      photoDao.savePhoto(photo)
      flowCollector.emit(photo)
    } else {
      throw WallpaperDownloadFailedException()
    }
  }

  private suspend fun downloadRawImage(deviceHeight: Int, photoApiModel: PhotoApiModel): String? {
    // We double the device height and download a square image to be
    // set a wallpaper. This is done to ensure good scaling on all devices.
    val requiredSize = deviceHeight * 2
    val wallpaperUrl = photoApiModel.urls.rawUrl + "&w=$requiredSize&h=$requiredSize&fit=crop"
    return wallpaperDownloader.downloadWallpaper(
      fileName = photoApiModel.id,
      wallpaperUrl = wallpaperUrl,
    )
  }

  private suspend fun downloadRegularImage(photoApiModel: PhotoApiModel): String? {
    val wallpaperUrl = photoApiModel.urls.regularUrl
    return wallpaperDownloader.downloadWallpaper(
      fileName = "${photoApiModel.id}_regular",
      wallpaperUrl = wallpaperUrl,
    )
  }

  private suspend fun downloadThumbImage(photoApiModel: PhotoApiModel): String? {
    val wallpaperUrl = photoApiModel.urls.thumbUrl
    return wallpaperDownloader.downloadWallpaper(
      fileName = "${photoApiModel.id}_thumb",
      wallpaperUrl = wallpaperUrl,
    )
  }

  suspend fun getCurrentPhoto(): Flow<Photo> =
    flow {
      val photo = photoDao.getPhoto()
      if (photo != null) {
        emit(photo)
      } else {
        throw PhotoNotFoundException()
      }
    }
      .flowOn(dispatcherProvider.io)
}
