package com.ratik.uttam

import android.app.Application
import com.ratik.uttam.di.Injector
import com.ratik.uttam.logging.ReleaseTree
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import timber.log.Timber.Forest.plant
import javax.inject.Inject

@HiltAndroidApp
class UttamApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initLogging()
    }

    private fun initLogging() {
        val logTree = if (BuildConfig.DEBUG) Timber.DebugTree() else ReleaseTree()
        plant(logTree)
    }
}