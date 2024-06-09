package com.ratik.uttam.ui.feature.settings

import com.ratik.uttam.core.BaseViewModel
import com.ratik.uttam.core.DispatcherProvider
import com.ratik.uttam.core.Ignored
import com.ratik.uttam.domain.UserRepo
import com.ratik.uttam.ui.feature.settings.SettingsAction.ToggleSetWallpaperAutomatically
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class SettingsViewModel @Inject constructor(
  dispatcherProvider: DispatcherProvider,
  private val userRepo: UserRepo,
) :
  BaseViewModel<SettingsState, SettingsAction>(SettingsState.initialState, dispatcherProvider) {
  init {
    initialize()
  }

  private fun initialize() {
    val setWallpaperAutomatically = userRepo.shouldSetWallpaperAutomatically()
    updateState { currentState ->
      currentState.copy(setWallpaperAutomatically = setWallpaperAutomatically)
    }
  }

  override fun onViewAction(viewAction: SettingsAction) {
    when (viewAction) {
      is ToggleSetWallpaperAutomatically -> {
        updateState { currentState ->
          currentState.copy(setWallpaperAutomatically = !currentState.setWallpaperAutomatically)
        }
        userRepo.toggleSetWallpaperAutomatically()
      }
    }
  }

  override fun handleError(throwable: Throwable) = Ignored
}
