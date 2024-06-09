package com.ratik.uttam.ui.feature.splash

import com.fueled.android.core.common.contract.NavigationTarget

internal sealed class SplashNavTarget : NavigationTarget {
  data object Landing : SplashNavTarget()

  data object Home : SplashNavTarget()
}
