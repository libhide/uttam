package com.ratik.uttam.ui.onboarding

import com.ratik.uttam.core.BaseViewModel
import com.ratik.uttam.core.DispatcherProvider
import com.ratik.uttam.core.Ignored
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
        TODO("Not yet implemented")
    }

    override fun handleError(throwable: Throwable) = Ignored
}