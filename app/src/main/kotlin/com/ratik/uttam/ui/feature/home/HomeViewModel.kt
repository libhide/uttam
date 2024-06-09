package com.ratik.uttam.ui.feature.home

import com.ratik.uttam.core.BaseViewModel
import com.ratik.uttam.core.DispatcherProvider
import com.ratik.uttam.core.contract.ViewEvent.Effect
import com.ratik.uttam.data.extensions.collectBy
import com.ratik.uttam.domain.PhotoRepo
import com.ratik.uttam.domain.UserRepo
import com.ratik.uttam.ui.feature.home.HomeAction.RefreshWallpaper
import com.ratik.uttam.ui.feature.home.HomeAction.SetWallpaper
import com.ratik.uttam.ui.feature.home.HomeEffect.LaunchCropAndSetWallpaperFlow
import com.ratik.uttam.ui.feature.home.HomeEffect.SetWallpaperSilently
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class HomeViewModel @Inject constructor(
  dispatcherProvider: DispatcherProvider,
  private val photoRepo: PhotoRepo,
  private val userRepo: UserRepo,
) : BaseViewModel<HomeState, HomeAction>(
  HomeState.initialState,
  dispatcherProvider,
) {

  init {
    initialize()
  }

  private fun initialize() {
    launch {
      photoRepo
        .getCurrentPhoto()
        .collectBy(
          onStart = { updateState { currentState -> currentState.copy(isLoading = true) } },
          onEach = { photo ->
            updateState { currentState ->
              currentState.copy(isLoading = false, currentWallpaper = photo)
            }
          },
          onError = { updateState { currentState -> currentState.copy(isLoading = false) } },
        )
    }
  }

  override fun onViewAction(viewAction: HomeAction) {
    when (viewAction) {
      RefreshWallpaper -> {
        launch {
          photoRepo
            .fetchRandomPhoto()
            .collectBy(
              onStart = { updateState { currentState -> currentState.copy(isLoading = true) } },
              onEach = { photo ->
                updateState { currentState ->
                  currentState.copy(isLoading = false, currentWallpaper = photo)
                }
                val shouldSetAutomatically = userRepo.shouldSetWallpaperAutomatically()
                if (shouldSetAutomatically) {
                  dispatchViewEvent(Effect(SetWallpaperSilently))
                }
              },
              onError = {
                updateState { currentState -> currentState.copy(isLoading = false) }
                handleError(it)
              },
            )
        }
      }

      SetWallpaper -> {
        dispatchViewEvent(Effect(LaunchCropAndSetWallpaperFlow))
      }
    }
  }

  override fun handleError(throwable: Throwable) {
    Timber.e(throwable)
  }
}
