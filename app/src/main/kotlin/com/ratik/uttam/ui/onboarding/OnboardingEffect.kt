package com.ratik.uttam.ui.onboarding

import com.fueled.android.core.common.contract.SideEffect

sealed class OnboardingEffect : SideEffect {
    object NavigateToHome : OnboardingEffect()
}