package com.ratik.uttam.ui.feature.landing

import com.fueled.android.core.common.contract.NavigationTarget

internal sealed class LandingNavTarget : NavigationTarget {
  data object Onboarding : LandingNavTarget()
}
