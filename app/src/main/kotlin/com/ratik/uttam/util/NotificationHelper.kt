package com.ratik.uttam.util

import android.Manifest.permission
import android.app.PendingIntent
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.ratik.uttam.MainActivity
import com.ratik.uttam.R
import com.ratik.uttam.core.DispatcherProvider
import com.ratik.uttam.domain.model.Photo
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class NotificationHelper @Inject constructor(
  private val notificationManager: NotificationManagerCompat,
  private val dispatcherProvider: DispatcherProvider,
) {

  suspend fun pushNewWallpaperNotification(context: Context, photo: Photo) {
    if (!notificationManager.areNotificationsEnabled() ||
      ActivityCompat.checkSelfPermission(context, permission.POST_NOTIFICATIONS) !=
      PackageManager.PERMISSION_GRANTED
    ) {
      return
    }

    withContext(dispatcherProvider.io) {
      val mainActivityIntent = Intent(context, MainActivity::class.java)
      val showWallpaperIntent = getActivity(
        context,
        OPEN_NEW_WALLPAPER_REQUEST_CODE,
        mainActivityIntent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
      )

      val largeWallpaperImage = decodeSampledBitmap(photo.regularPhotoUri, MAX_BIG_PICTURE_SIZE)
      val thumbWallpaperImage = decodeSampledBitmap(photo.thumbPhotoUri, MAX_LARGE_ICON_SIZE)
      val builder = createNewWallpaperNotification(
        context = context,
        photographerName = photo.photographer.name,
        thumbWallpaperImage = thumbWallpaperImage,
        largeWallpaperImage = largeWallpaperImage,
        showWallpaperIntent = showWallpaperIntent,
      )

      notificationManager.notify(NEW_WALLPAPER_NOTIFICATION_ID, builder.build())
    }
  }

  private fun createNewWallpaperNotification(
    context: Context,
    photographerName: String,
    thumbWallpaperImage: Bitmap?,
    largeWallpaperImage: Bitmap?,
    showWallpaperIntent: PendingIntent?,
  ) = NotificationCompat.Builder(context, CHANNEL_ID)
    .setSmallIcon(R.drawable.ic_stat_uttam)
    .setContentTitle(context.getString(R.string.wallpaper_notif_title))
    .setContentText(context.getString(R.string.wallpaper_notif_photo_by) + photographerName)
    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
    .setLargeIcon(thumbWallpaperImage)
    .setAutoCancel(true)
    .setStyle(
      NotificationCompat.BigPictureStyle()
        .bigPicture(largeWallpaperImage)
        .setBigContentTitle(context.getString(R.string.wallpaper_notif_title)),
    )
    .setContentIntent(showWallpaperIntent)

  private fun decodeSampledBitmap(filePath: String, maximumSize: Int): Bitmap? {
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    BitmapFactory.decodeFile(filePath, bounds)
    if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null

    var sampleSize = 1
    while (bounds.outWidth / sampleSize > maximumSize ||
      bounds.outHeight / sampleSize > maximumSize
    ) {
      sampleSize *= 2
    }

    val options = BitmapFactory.Options().apply { inSampleSize = sampleSize }
    return BitmapFactory.decodeFile(filePath, options)
  }

  companion object {
    const val CHANNEL_ID = "uttam"
    const val CHANNEL_NAME = "General"
    const val NEW_WALLPAPER_NOTIFICATION_ID = 1
    const val OPEN_NEW_WALLPAPER_REQUEST_CODE = 1
    private const val MAX_BIG_PICTURE_SIZE = 1024
    private const val MAX_LARGE_ICON_SIZE = 256
  }
}
