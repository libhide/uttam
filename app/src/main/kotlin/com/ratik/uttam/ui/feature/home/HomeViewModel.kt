package com.ratik.uttam.ui.feature.home

import com.ratik.uttam.R
import com.ratik.uttam.core.BaseViewModel
import com.ratik.uttam.core.DispatcherProvider
import com.ratik.uttam.core.MessageState.Snack
import com.ratik.uttam.core.contract.ViewEvent.DisplayMessage
import com.ratik.uttam.core.contract.ViewEvent.Effect
import com.ratik.uttam.data.extensions.collectBy
import com.ratik.uttam.domain.PhotoRepo
import com.ratik.uttam.domain.UserRepo
import com.ratik.uttam.domain.WallpaperSetter
import com.ratik.uttam.ui.feature.home.HomeAction.RefreshWallpaper
import com.ratik.uttam.ui.feature.home.HomeAction.SetWallpaper
import com.ratik.uttam.ui.feature.home.HomeEffect.LaunchCropAndSetWallpaperFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class HomeViewModel @Inject constructor(
  dispatcherProvider: DispatcherProvider,
  private val photoRepo: PhotoRepo,
  private val userRepo: UserRepo,
  private val wallpaperSetter: WallpaperSetter,
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
                if (userRepo.shouldSetWallpaperAutomatically()) {
                  wallpaperSetter.setHomeScreen(photo.rawPhotoUri)
                    .onSuccess {
                      dispatchViewEvent(
                        DisplayMessage(Snack(resourceProvider.getString(R.string.wallpaper_set_text))),
                      )
                    }
                    .onFailure { error ->
                      handleError(error)
                      dispatchViewEvent(
                        DisplayMessage(Snack(resourceProvider.getString(R.string.generic_error))),
                      )
                    }
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
        currentState.currentWallpaper?.let { photo ->
          dispatchViewEvent(Effect(LaunchCropAndSetWallpaperFlow(photo.rawPhotoUri)))
        }
      }
    }
  }

  override fun handleError(throwable: Throwable) {
    Timber.e(throwable)
  }
}
