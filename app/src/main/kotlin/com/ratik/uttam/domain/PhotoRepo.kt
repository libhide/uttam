package com.ratik.uttam.domain

import android.os.Environment
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
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.nio.channels.FileChannel
import javax.inject.Inject

internal class PhotoRepo @Inject constructor(
    private val unsplashApi: UnsplashApi,
    private val dispatcherProvider: DispatcherProvider,
    private val wallpaperDownloader: WallpaperDownloader,
    private val photoDao: PhotoDao,
    private val mapper: PhotoMapper,
) {
    private val extStorageDir: File
        get() {
            val appName = "Uttam"
            return File(
                Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES
                ), appName
            )
        }

    suspend fun fetchRandomPhoto(): Flow<Photo> = flow {
        unsplashApi.getRandomPhotoSus(
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
            Timber.e("Error fetching photo: $error")
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

    suspend fun copyFileToExternalStorage(
        sourceFile: File,
        exportedFileFilename: String,
    ): Flow<File> = flow {
        val destination = extStorageDir
        if (!destination.exists() && !destination.mkdirs()) {
            val t = Throwable("Error making directory: " + destination.path)
            throw t
        }
        val exportedFile = File(destination, exportedFileFilename)
        if (exportedFile.exists()) {
            emit(exportedFile)
        } else {
            var inChannel: FileChannel? = null
            var outChannel: FileChannel? = null
            try {
                inChannel = FileInputStream(sourceFile).channel
                outChannel = FileOutputStream(exportedFile).channel
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
            try {
                inChannel?.transferTo(0, inChannel.size(), outChannel)
                    ?: throw IOException("inChannel size is null")
            } finally {
                inChannel?.close()
                outChannel?.close()
            }
            emit(exportedFile)
        }
    }.flowOn(dispatcherProvider.io)
}