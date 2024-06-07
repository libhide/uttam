package com.ratik.uttam.ui.onboarding

internal sealed class OnboardingAction {
    object ShowNextStep : OnboardingAction()
    object NotificationPermissionResponded : OnboardingAction()
    object FinishOnboarding : OnboardingAction()
}