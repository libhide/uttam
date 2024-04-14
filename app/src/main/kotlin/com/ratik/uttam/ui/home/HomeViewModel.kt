package com.ratik.uttam.ui.home

import com.ratik.uttam.core.BaseViewModel
import com.ratik.uttam.core.DispatcherProvider
import com.ratik.uttam.core.contract.ViewEvent
import com.ratik.uttam.core.contract.ViewEvent.Effect
import com.ratik.uttam.data.extensions.collectBy
import com.ratik.uttam.domain.PhotoRepo
import com.ratik.uttam.ui.home.HomeAction.RefreshWallpaper
import com.ratik.uttam.ui.home.HomeEffect.ChangeWallpaper
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
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
                launch {
                    photoRepo.fetchRandomPhoto().collectBy(onStart = {
                        updateState { currentState ->
                            currentState.copy(isLoading = true)
                        }
                    }, onEach = { photo ->
                        updateState { currentState ->
                            currentState.copy(
                                isLoading = false,
                                currentWallpaper = photo,
                            )
                        }
                        dispatchViewEvent(Effect(ChangeWallpaper))
                    }, onError = {
                        updateState { currentState ->
                            currentState.copy(
                                isLoading = false,
                            )
                        }
                        handleError(it)
                    })
                }
            }
        }
    }

    override fun handleError(throwable: Throwable) {
        Timber.e(throwable)
    }
}