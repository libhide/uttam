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
  private val notificationBuilder: NotificationCompat.Builder,
  private val notificationManager: NotificationManagerCompat,
  private val dispatcherProvider: DispatcherProvider,
) {

  suspend fun pushNewWallpaperNotification(context: Context, photo: Photo) {
    if (ActivityCompat.checkSelfPermission(
        context,
        permission.POST_NOTIFICATIONS,
      ) != PackageManager.PERMISSION_GRANTED
    ) {
      return
    }

    return withContext(dispatcherProvider.io) {
      val mainActivityIntent = Intent(context, MainActivity::class.java)

      val showWallpaperIntent = getActivity(
        context,
        OPEN_NEW_WALLPAPER_REQUEST_CODE,
        mainActivityIntent,
        PendingIntent.FLAG_IMMUTABLE,
      )

      val largeWallpaperImage = BitmapFactory.decodeFile(photo.regularPhotoUri)
      val thumbWallpaperImage = BitmapFactory.decodeFile(photo.thumbPhotoUri)

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
  ) = notificationBuilder
    .setSmallIcon(R.drawable.ic_stat_uttam)
    .setContentTitle(context.getString(R.string.wallpaper_notif_title))
    .setContentText(
      context.getString(R.string.wallpaper_notif_photo_by) +
        photographerName,
    )
    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
    .setLargeIcon(thumbWallpaperImage)
    .setAutoCancel(true)
    .setStyle(
      NotificationCompat.BigPictureStyle()
        .bigPicture(largeWallpaperImage)
        .setBigContentTitle(context.getString(R.string.wallpaper_notif_title)),
    )
    .setContentIntent(showWallpaperIntent)

  companion object {
    const val CHANNEL_ID = "uttam"
    const val CHANNEL_NAME = "General"
    const val NEW_WALLPAPER_NOTIFICATION_ID = 1
    const val OPEN_NEW_WALLPAPER_REQUEST_CODE = 1
  }
}
