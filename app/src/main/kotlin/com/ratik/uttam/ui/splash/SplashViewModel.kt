package com.ratik.uttam.ui.splash

import com.ratik.uttam.core.BaseViewModel
import com.ratik.uttam.core.DispatcherProvider
import com.ratik.uttam.core.Ignored
import com.ratik.uttam.core.contract.ViewEvent.Navigate
import com.ratik.uttam.domain.UserRepo
import com.ratik.uttam.ui.splash.SplashNavTarget.Home
import com.ratik.uttam.ui.splash.SplashNavTarget.Landing
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import javax.inject.Inject

@HiltViewModel
internal class SplashViewModel @Inject constructor(
    dispatcherProvider: DispatcherProvider,
    private val userRepo: UserRepo,
) : BaseViewModel<SplashState, SplashAction>(
    SplashState.initialState,
    dispatcherProvider,
) {

    init {
        launch {
            delay(1000L)
            initialise()
        }
    }

    private fun initialise() {
        val hasOnboarded = userRepo.hasOnboarded()
        if (hasOnboarded) {
            dispatchViewEvent(Navigate(Home))
        } else {
            dispatchViewEvent(Navigate(Landing))
        }
    }

    override fun onViewAction(viewAction: SplashAction) = Ignored

    override fun handleError(throwable: Throwable) = Ignored
}

private const val SPLASH_DELAY = 1000L