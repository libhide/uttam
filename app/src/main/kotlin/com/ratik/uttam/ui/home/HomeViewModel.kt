package com.ratik.uttam.ui.home

import com.ratik.uttam.core.BaseViewModel
import com.ratik.uttam.core.DispatcherProvider
import javax.inject.Inject

internal class HomeViewModel @Inject constructor(
    dispatcherProvider: DispatcherProvider,
) : BaseViewModel<HomeState, HomeAction>(
    HomeState.initialState,
    dispatcherProvider,
) {

    override fun onViewAction(viewAction: HomeAction) {
        TODO("Not yet implemented")
    }

    override fun handleError(throwable: Throwable) {
        TODO("Not yet implemented")
    }
}