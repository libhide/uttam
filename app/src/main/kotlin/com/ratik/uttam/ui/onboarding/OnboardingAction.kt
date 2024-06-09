package com.ratik.uttam.ui.onboarding

internal sealed class OnboardingAction {
  object ShowNextStep : OnboardingAction()

  object NotificationPermissionResponded : OnboardingAction()

  data class FinishOnboarding(val deviceWidth: Int, val deviceHeight: Int) : OnboardingAction()
}
