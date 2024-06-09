package com.ratik.uttam.ui.splash

import com.fueled.android.core.common.contract.NavigationTarget

internal sealed class SplashNavTarget : NavigationTarget {
  object Landing : SplashNavTarget()

  object Home : SplashNavTarget()
}
