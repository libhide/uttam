package com.ratik.uttam.ui.feature.onboarding

import com.ratik.uttam.core.MessageState
import com.ratik.uttam.core.contract.BaseState
import com.ratik.uttam.ui.feature.onboarding.model.OnboardingStep
import com.ratik.uttam.ui.feature.onboarding.model.OnboardingStep.DONE
import com.ratik.uttam.ui.feature.onboarding.model.OnboardingStep.FULL_CONTROL
import com.ratik.uttam.ui.feature.onboarding.model.OnboardingStep.NOTIF_PERMISSION
import com.ratik.uttam.ui.feature.onboarding.model.OnboardingStep.WELCOME

internal data class OnboardingState(
  override val isLoading: Boolean = false,
  override val errorState: MessageState? = null,
  val onboardingSteps: List<OnboardingStep> = listOf(WELCOME, NOTIF_PERMISSION, FULL_CONTROL, DONE),
  val currentStepIndex: Int = 0,
) : BaseState {
  companion object {
    val initialState = OnboardingState()
  }
}
