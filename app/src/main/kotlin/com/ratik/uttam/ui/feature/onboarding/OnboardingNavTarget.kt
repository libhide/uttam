package com.ratik.uttam.ui.feature.onboarding

import com.fueled.android.core.common.contract.NavigationTarget

internal sealed class OnboardingNavTarget : NavigationTarget {
  data object Home : OnboardingNavTarget()
}
