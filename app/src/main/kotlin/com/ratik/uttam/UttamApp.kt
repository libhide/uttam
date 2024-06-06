package com.ratik.uttam

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.ratik.uttam.bg.RefreshWallpaperWorker
import com.ratik.uttam.logging.ReleaseTree
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import timber.log.Timber.Forest.plant
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class UttamApp : Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        initLogging()
        enqueueRefreshWallpaperRequest()
    }

    private fun initLogging() {
        val logTree = if (BuildConfig.DEBUG) Timber.DebugTree() else ReleaseTree()
        plant(logTree)
    }

    private fun enqueueRefreshWallpaperRequest() {
        val workConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED) // requires WiFi
            .build()

        val repeatInterval = TimeUnit.HOURS.toMillis(1)
        // val initialDelay = TimeUnit.HOURS.toMillis(1)

        val workRequest = PeriodicWorkRequest.Builder(
            RefreshWallpaperWorker::class.java,
            repeatInterval,
            TimeUnit.MILLISECONDS
        ).setConstraints(workConstraints)
            // .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            RefreshWallpaperWorker::class.java.simpleName,
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}