package com.ratik.uttam.data.storage

import android.content.Context
import com.ratik.uttam.R
import com.ratik.uttam.core.DispatcherProvider
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException
import javax.inject.Inject

class WallpaperDownloader @Inject constructor(
  private val dispatcherProvider: DispatcherProvider,
  private val httpClient: OkHttpClient,
  context: Context,
) {
  private val appCacheFolder =
    File(context.filesDir, context.getString(R.string.app_name).lowercase())

  suspend fun downloadWallpaper(fileName: String, wallpaperUrl: String): String =
    withContext(dispatcherProvider.io) {
      ensureCacheFolderExists()

      val destination = File(appCacheFolder, "$fileName.jpg")
      val temporaryFile = File.createTempFile(fileName, ".tmp", appCacheFolder)
      val request = Request.Builder().url(wallpaperUrl).build()

      try {
        httpClient.newCall(request).execute().use { response ->
          if (!response.isSuccessful) {
            throw IOException("Wallpaper download failed with HTTP ${response.code}")
          }
          val body = response.body ?: throw IOException("Wallpaper download returned no data")
          body.byteStream().use { input ->
            temporaryFile.outputStream().buffered().use { output -> input.copyTo(output) }
          }
        }

        if (temporaryFile.length() == 0L) {
          throw IOException("Wallpaper download returned an empty file")
        }
        if (!temporaryFile.renameTo(destination)) {
          throw IOException("Could not finalize wallpaper download")
        }

        destination.absolutePath
      } finally {
        temporaryFile.delete()
      }
    }

  fun deleteFiles(filePaths: Collection<String>) {
    filePaths.forEach { filePath -> File(filePath).delete() }
  }

  fun cleanStaleFilesExcept(retainedFilePaths: Set<String>) {
    if (appCacheFolder.exists()) {
      val staleBefore = System.currentTimeMillis() - STALE_FILE_AGE_MILLIS
      appCacheFolder.listFiles()
        ?.filter { file ->
          file.absolutePath !in retainedFilePaths && file.lastModified() < staleBefore
        }
        ?.forEach { file -> file.delete() }
    }
  }

  private fun ensureCacheFolderExists() {
    if (!appCacheFolder.exists() && !appCacheFolder.mkdirs()) {
      throw IOException("Could not create wallpaper storage")
    }
  }

  private companion object {
    const val STALE_FILE_AGE_MILLIS = 4 * 60 * 60 * 1000L
  }
}
