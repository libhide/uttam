package com.ratik.uttam.ui.onboarding

import com.ratik.uttam.core.BaseViewModel
import com.ratik.uttam.core.DispatcherProvider
import com.ratik.uttam.core.Ignored
import com.ratik.uttam.core.contract.ViewEvent.Navigate
import com.ratik.uttam.ui.onboarding.OnboardingAction.FinishOnboarding
import com.ratik.uttam.ui.onboarding.OnboardingAction.NotificationPermissionResponded
import com.ratik.uttam.ui.onboarding.OnboardingAction.ShowNextStep
import com.ratik.uttam.ui.onboarding.OnboardingNavTarget.Home
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class OnboardingViewModel @Inject constructor(
    dispatcherProvider: DispatcherProvider,
) : BaseViewModel<OnboardingState, OnboardingAction>(
    OnboardingState.initialState,
    dispatcherProvider,
) {

    override fun onViewAction(viewAction: OnboardingAction) {
        when (viewAction) {
            is ShowNextStep -> {
                updateState { currentState ->
                    currentState.copy(
                        currentStepIndex = currentState.currentStepIndex + 1
                    )
                }
            }

            NotificationPermissionResponded -> {
                updateState { currentState ->
                    currentState.copy(
                        currentStepIndex = currentState.currentStepIndex + 1
                    )
                }
            }

            is FinishOnboarding -> {
                // TODO: set onboarding as complete
                dispatchViewEvent(Navigate(Home))
            }
        }
    }

    override fun handleError(throwable: Throwable) = Ignored
}