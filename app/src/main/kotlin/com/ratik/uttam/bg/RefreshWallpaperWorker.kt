package com.ratik.uttam.bg

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ratik.uttam.R
import com.ratik.uttam.core.DispatcherProvider
import com.ratik.uttam.domain.PhotoRepo
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.withContext
import timber.log.Timber

@HiltWorker
internal class RefreshWallpaperWorker
@AssistedInject
constructor(
  @Assisted val appContext: Context,
  @Assisted params: WorkerParameters,
  private val photoRepo: PhotoRepo,
  private val dispatcherProvider: DispatcherProvider,
) : CoroutineWorker(appContext, params) {

  override suspend fun doWork(): Result {
    return withContext(dispatcherProvider.io) {
      try {
        var workResult = Result.failure()
        photoRepo.fetchRandomPhoto().collect { photo ->
          val builder =
            NotificationCompat.Builder(appContext, "UttamChannel")
              .setSmallIcon(R.drawable.ic_stat_uttam)
              .setContentTitle(appContext.getString(R.string.wallpaper_notif_title))
              .setContentText(
                appContext.getString(R.string.wallpaper_notif_photo_by) + photo.photographer.name
              )
              .setPriority(NotificationCompat.PRIORITY_DEFAULT)
          with(NotificationManagerCompat.from(appContext)) {
            if (
              ActivityCompat.checkSelfPermission(
                appContext,
                Manifest.permission.POST_NOTIFICATIONS,
              ) == PackageManager.PERMISSION_GRANTED
            ) {
              notify(1, builder.build())
            }
          }
          workResult = Result.success()
        }
        workResult
      } catch (exception: Exception) {
        Timber.d("RATIK: Photo fetch error.")
        Timber.e(exception.message)
        Result.failure()
      }
    }
  }
}
