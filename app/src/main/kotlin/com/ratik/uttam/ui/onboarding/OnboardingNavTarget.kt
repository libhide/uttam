package com.ratik.uttam.ui.onboarding

import com.fueled.android.core.common.contract.NavigationTarget

internal sealed class OnboardingNavTarget : NavigationTarget {
  object Home : OnboardingNavTarget()
}
