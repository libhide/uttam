package com.ratik.uttam.ui.feature.onboarding

internal sealed class OnboardingAction {
  data object ShowNextStep : OnboardingAction()

  data object NotificationPermissionResponded : OnboardingAction()

  data class FinishOnboarding(val deviceWidth: Int, val deviceHeight: Int) : OnboardingAction()
}
