package com.ratik.uttam

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
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

  override fun onCreate() {
    super.onCreate()
    initLogging()
    createNotificationChannel()
  }

  private fun initLogging() {
    val logTree = if (BuildConfig.DEBUG) Timber.DebugTree() else ReleaseTree()
    plant(logTree)
  }

  override val workManagerConfiguration: Configuration
    get() = Configuration.Builder().setWorkerFactory(workerFactory).build()

  private fun createNotificationChannel() {
    if (SDK_INT >= O) {
      val importance = NotificationManager.IMPORTANCE_DEFAULT
      val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
      notificationManager.createNotificationChannel(channel)
    }
  }
}
