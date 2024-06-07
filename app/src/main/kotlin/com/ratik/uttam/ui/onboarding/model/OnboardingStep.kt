package com.ratik.uttam.ui.onboarding.model

enum class OnboardingStep {
    WELCOME,
    NOTIF_PERMISSION,
    FULL_CONTROL,
    DONE,
}

fun OnboardingStep.requiresNotificationPermission(): Boolean {
    return this == OnboardingStep.NOTIF_PERMISSION
}