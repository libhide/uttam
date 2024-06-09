package com.ratik.uttam.ui.feature.home

import com.ratik.uttam.core.MessageState
import com.ratik.uttam.core.contract.BaseState
import com.ratik.uttam.domain.model.Photo

internal data class HomeState(
  override val isLoading: Boolean = false,
  override val errorState: MessageState? = null,
  val currentWallpaper: Photo? = null,
) : BaseState {
  companion object {
    val initialState = HomeState()
  }
}
