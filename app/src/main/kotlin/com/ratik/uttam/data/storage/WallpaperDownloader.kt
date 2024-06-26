package com.ratik.uttam.data.storage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.ratik.uttam.R
import com.ratik.uttam.core.DispatcherProvider
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

class WallpaperDownloader @Inject constructor(
  private val dispatcherProvider: DispatcherProvider,
  context: Context,
) {
  private val appCacheFolder =
    File(context.filesDir, context.getString(R.string.app_name).lowercase())

  suspend fun downloadWallpaper(fileName: String, wallpaperUrl: String): String? {
    return withContext(dispatcherProvider.io) {
      val bitmap: Bitmap?
      try {
        val url = URL(wallpaperUrl)
        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
        connection.doInput = true
        connection.connect()
        val input = connection.inputStream
        bitmap = BitmapFactory.decodeStream(input)
        saveBitmapToInternalStorage(fileName, bitmap)
      } catch (e: Exception) {
        e.printStackTrace()
        throw e
      }
    }
  }

  fun clearCacheFolder() {
    if (appCacheFolder.exists()) {
      appCacheFolder.listFiles()?.forEach { file -> file.delete() }
    }
  }

  private fun saveBitmapToInternalStorage(fileName: String, bitmap: Bitmap?): String? {
    return bitmap?.let {
      if (!appCacheFolder.exists()) {
        appCacheFolder.mkdirs()
      }
      val file = File(appCacheFolder, "$fileName.jpg")
      val outputStream: FileOutputStream
      try {
        outputStream = FileOutputStream(file)
        it.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.close()
        file.absolutePath
      } catch (e: Exception) {
        e.printStackTrace()
        throw e
      }
    }
  }
}
