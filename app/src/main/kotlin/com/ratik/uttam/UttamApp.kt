package com.ratik.uttam

import android.app.Application
import com.ratik.uttam.logging.ReleaseTree
import timber.log.Timber
import timber.log.Timber.Forest.plant

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