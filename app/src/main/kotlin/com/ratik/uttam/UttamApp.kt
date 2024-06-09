package com.ratik.uttam

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.ratik.uttam.logging.ReleaseTree
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import timber.log.Timber
import timber.log.Timber.Forest.plant

@HiltAndroidApp
class UttamApp : Application(), Configuration.Provider {

  @Inject lateinit var workerFactory: HiltWorkerFactory

  override fun onCreate() {
    super.onCreate()
    initLogging()
  }

  private fun initLogging() {
    val logTree = if (BuildConfig.DEBUG) Timber.DebugTree() else ReleaseTree()
    plant(logTree)
  }

  override val workManagerConfiguration: Configuration
    get() = Configuration.Builder().setWorkerFactory(workerFactory).build()
}
