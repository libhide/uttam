package com.ratik.uttam.ui.feature.onboarding

import com.ratik.uttam.core.BaseViewModel
import com.ratik.uttam.core.DispatcherProvider
import com.ratik.uttam.core.Ignored
import com.ratik.uttam.core.MessageState.Snack
import com.ratik.uttam.core.contract.ViewEvent.DisplayMessage
import com.ratik.uttam.core.contract.ViewEvent.Navigate
import com.ratik.uttam.data.extensions.collectBy
import com.ratik.uttam.domain.PhotoRepo
import com.ratik.uttam.domain.UserRepo
import com.ratik.uttam.ui.feature.onboarding.OnboardingAction.FinishOnboarding
import com.ratik.uttam.ui.feature.onboarding.OnboardingAction.NotificationPermissionResponded
import com.ratik.uttam.ui.feature.onboarding.OnboardingAction.ShowNextStep
import com.ratik.uttam.ui.feature.onboarding.OnboardingNavTarget.Home
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class OnboardingViewModel
@Inject
constructor(
  dispatcherProvider: DispatcherProvider,
  private val userRepo: UserRepo,
  private val photoRepo: PhotoRepo,
) :
  BaseViewModel<OnboardingState, OnboardingAction>(
    OnboardingState.initialState,
    dispatcherProvider,
  ) {

  override fun onViewAction(viewAction: OnboardingAction) {
    when (viewAction) {
      is ShowNextStep -> {
        updateState { currentState ->
          currentState.copy(currentStepIndex = currentState.currentStepIndex + 1)
        }
      }

      NotificationPermissionResponded -> {
        updateState { currentState ->
          currentState.copy(currentStepIndex = currentState.currentStepIndex + 1)
        }
      }

      is FinishOnboarding -> {
        userRepo.setDeviceHeight(viewAction.deviceHeight)
        userRepo.setDeviceWidth(viewAction.deviceWidth)
        launch {
          photoRepo
            .fetchDefaultPhoto()
            .collectBy(
              onStart = { updateState { currentState -> currentState.copy(isLoading = true) } },
              onEach = {
                userRepo.setHasOnboarded()
                dispatchViewEvent(Navigate(Home))
              },
              onError = {
                // TODO: Handle error
                dispatchViewEvent(DisplayMessage(Snack("Error fetching photo")))
              },
            )
        }
      }
    }
  }

  override fun handleError(throwable: Throwable) = Ignored
}
