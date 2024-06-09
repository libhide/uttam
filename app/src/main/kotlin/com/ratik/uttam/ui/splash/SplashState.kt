package com.ratik.uttam.ui.splash

import com.ratik.uttam.core.MessageState
import com.ratik.uttam.core.contract.BaseState

class SplashState(
  override val isLoading: Boolean = false,
  override val errorState: MessageState? = null,
) : BaseState {
  companion object {
    val initialState = SplashState()
  }
}
