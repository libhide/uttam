package com.ratik.uttam.logging

import android.util.Log
import timber.log.Timber.DebugTree

class ReleaseTree : DebugTree() {
  override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
    if (priority == Log.ERROR || priority == Log.WARN) {
      super.log(priority, tag, message, t)
    }
  }
}
