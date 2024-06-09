package com.ratik.uttam.ui.feature.settings

import com.ratik.uttam.core.MessageState
import com.ratik.uttam.core.contract.BaseState

internal data class SettingsState(
  override val isLoading: Boolean = false,
  override val errorState: MessageState? = null,
  val setWallpaperAutomatically: Boolean = true,
) : BaseState {
  companion object {
    val initialState = SettingsState()
  }
}
