package com.ratik.uttam.bg

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ratik.uttam.data.exceptions.UnauthorizedException
import com.ratik.uttam.domain.PhotoRepo
import com.ratik.uttam.domain.UserRepo
import com.ratik.uttam.domain.WallpaperSetter
import com.ratik.uttam.util.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import timber.log.Timber

@HiltWorker
internal class RefreshWallpaperWorker @AssistedInject constructor(
  @Assisted appContext: Context,
  @Assisted params: WorkerParameters,
  private val photoRepo: PhotoRepo,
  private val userRepo: UserRepo,
  private val wallpaperSetter: WallpaperSetter,
  private val notificationHelper: NotificationHelper,
) : CoroutineWorker(appContext, params) {

  override suspend fun doWork(): Result {
    return try {
      val photo = photoRepo.fetchRandomPhoto().first()
      if (userRepo.shouldSetWallpaperAutomatically()) {
        wallpaperSetter.setHomeScreen(photo.rawPhotoUri)
          .onFailure { error -> Timber.w(error, "Unable to set the refreshed wallpaper") }
      }
      notificationHelper.pushNewWallpaperNotification(applicationContext, photo)
      Result.success()
    } catch (exception: CancellationException) {
      throw exception
    } catch (exception: UnauthorizedException) {
      Timber.e(exception, "Unsplash rejected the wallpaper refresh")
      Result.failure()
    } catch (exception: Exception) {
      Timber.e(exception, "Wallpaper refresh failed")
      if (runAttemptCount < MAX_RETRY_ATTEMPTS) Result.retry() else Result.failure()
    }
  }

  private companion object {
    const val MAX_RETRY_ATTEMPTS = 3
  }
}
