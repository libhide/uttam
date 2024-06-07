package com.ratik.uttam.ui.landing

import com.ratik.uttam.core.BaseViewModel
import com.ratik.uttam.core.DispatcherProvider
import com.ratik.uttam.core.Ignored
import com.ratik.uttam.core.contract.ViewEvent.Navigate
import com.ratik.uttam.ui.landing.LandingAction.GetStarted
import com.ratik.uttam.ui.landing.LandingNavTarget.Onboarding
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class LandingViewModel @Inject constructor(
    dispatcherProvider: DispatcherProvider,
) : BaseViewModel<LandingState, LandingAction>(
    LandingState.initialState,
    dispatcherProvider,
) {

    override fun onViewAction(viewAction: LandingAction) {
        when (viewAction) {
            is GetStarted -> dispatchViewEvent(Navigate(Onboarding))
        }
    }

    override fun handleError(throwable: Throwable) = Ignored
}