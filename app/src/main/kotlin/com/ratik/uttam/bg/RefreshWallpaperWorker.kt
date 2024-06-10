package com.ratik.uttam.bg

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ratik.uttam.core.DispatcherProvider
import com.ratik.uttam.domain.PhotoRepo
import com.ratik.uttam.util.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.withContext
import timber.log.Timber

@HiltWorker
internal class RefreshWallpaperWorker @AssistedInject constructor(
  @Assisted val appContext: Context,
  @Assisted params: WorkerParameters,
  private val photoRepo: PhotoRepo,
  private val dispatcherProvider: DispatcherProvider,
  private val notificationHelper: NotificationHelper,
) : CoroutineWorker(appContext, params) {

  override suspend fun doWork(): Result {
    return withContext(dispatcherProvider.io) {
      try {
        var workResult = Result.failure()
        photoRepo.fetchRandomPhoto().collect { photo ->
          notificationHelper.pushNewWallpaperNotification(appContext, photo)
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
