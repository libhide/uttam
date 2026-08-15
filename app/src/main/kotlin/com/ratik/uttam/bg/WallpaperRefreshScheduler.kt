package com.ratik.uttam.bg

import android.content.Context
import android.content.SharedPreferences
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WallpaperRefreshScheduler @Inject constructor(
  private val context: Context,
  private val sharedPreferences: SharedPreferences,
) {
  fun scheduleDailyRefresh() {
    val constraints = Constraints.Builder()
      .setRequiredNetworkType(NetworkType.CONNECTED)
      .build()
    val workRequest =
      PeriodicWorkRequestBuilder<RefreshWallpaperWorker>(1, TimeUnit.DAYS)
        .setConstraints(constraints)
        .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
        .setInitialDelay(calculateDelayUntilMorning(), TimeUnit.MILLISECONDS)
        .build()

    val policy =
      if (sharedPreferences.getInt(SCHEDULE_VERSION_KEY, 0) < SCHEDULE_VERSION) {
        ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE
      } else {
        ExistingPeriodicWorkPolicy.UPDATE
      }
    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
      UNIQUE_WORK_NAME,
      policy,
      workRequest,
    )
    sharedPreferences.edit().putInt(SCHEDULE_VERSION_KEY, SCHEDULE_VERSION).apply()
  }

  private fun calculateDelayUntilMorning(): Long {
    val now = Calendar.getInstance()
    val nextRun = Calendar.getInstance().apply {
      set(Calendar.HOUR_OF_DAY, REFRESH_HOUR)
      set(Calendar.MINUTE, 0)
      set(Calendar.SECOND, 0)
      set(Calendar.MILLISECOND, 0)
      if (!after(now)) add(Calendar.DAY_OF_YEAR, 1)
    }
    return nextRun.timeInMillis - now.timeInMillis
  }

  private companion object {
    const val UNIQUE_WORK_NAME = "RatikUttamRefresh"
    const val REFRESH_HOUR = 7
    const val SCHEDULE_VERSION_KEY = "wallpaperRefreshScheduleVersion"
    const val SCHEDULE_VERSION = 1
  }
}
