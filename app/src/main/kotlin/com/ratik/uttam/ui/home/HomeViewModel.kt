package com.ratik.uttam.ui.home

import com.ratik.uttam.core.BaseViewModel
import com.ratik.uttam.core.DispatcherProvider
import com.ratik.uttam.core.MessageState
import com.ratik.uttam.core.MessageState.Snack
import com.ratik.uttam.core.contract.ViewEvent
import com.ratik.uttam.core.contract.ViewEvent.DisplayMessage
import com.ratik.uttam.domain.PhotoRepo
import com.ratik.uttam.ui.home.HomeAction.RefreshWallpaper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class HomeViewModel @Inject constructor(
    dispatcherProvider: DispatcherProvider,
    private val photoRepo: PhotoRepo,
) : BaseViewModel<HomeState, HomeAction>(
    HomeState.initialState,
    dispatcherProvider,
) {

    override fun onViewAction(viewAction: HomeAction) {
        when (viewAction) {
            RefreshWallpaper -> {
                dispatchViewEvent(DisplayMessage(Snack("Refreshing...")))
                launch { photoRepo.getRandomPhoto() }
            }
        }
    }

    override fun handleError(throwable: Throwable) = Unit
}