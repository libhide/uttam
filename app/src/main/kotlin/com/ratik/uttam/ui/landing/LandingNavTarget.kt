package com.ratik.uttam.ui.landing

import com.fueled.android.core.common.contract.NavigationTarget

internal sealed class LandingNavTarget : NavigationTarget {
  object Onboarding : LandingNavTarget()
}
