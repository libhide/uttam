package com.ratik.uttam

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.SharedPreferences
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.ratik.uttam.bg.WallpaperRefreshScheduler
import com.ratik.uttam.data.dao.UserDao.Companion.HAS_ONBOARDED
import com.ratik.uttam.logging.ReleaseTree
import com.ratik.uttam.util.NotificationHelper.Companion.CHANNEL_ID
import com.ratik.uttam.util.NotificationHelper.Companion.CHANNEL_NAME
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import timber.log.Timber.Forest.plant
import javax.inject.Inject

@HiltAndroidApp
class UttamApp : Application(), Configuration.Provider {

  @Inject
  lateinit var workerFactory: HiltWorkerFactory

  @Inject
  lateinit var notificationManager: NotificationManagerCompat

  @Inject
  lateinit var sharedPreferences: SharedPreferences

  @Inject
  lateinit var refreshScheduler: WallpaperRefreshScheduler

  override fun onCreate() {
    super.onCreate()
    initLogging()
    createNotificationChannel()
    updateWallpaperRefreshSchedule()
  }

  private fun initLogging() {
    val logTree = if (BuildConfig.DEBUG) Timber.DebugTree() else ReleaseTree()
    plant(logTree)
  }

  override val workManagerConfiguration: Configuration
    get() = Configuration.Builder().setWorkerFactory(workerFactory).build()

  private fun updateWallpaperRefreshSchedule() {
    if (sharedPreferences.getBoolean(HAS_ONBOARDED, false)) {
      refreshScheduler.scheduleDailyRefresh()
    }
  }

  private fun createNotificationChannel() {
    if (SDK_INT >= O) {
      val importance = NotificationManager.IMPORTANCE_DEFAULT
      val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
      notificationManager.createNotificationChannel(channel)
    }
  }
}
