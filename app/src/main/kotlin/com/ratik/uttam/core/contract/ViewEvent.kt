package com.ratik.uttam.core.contract

import com.fueled.android.core.common.contract.NavigationTarget
import com.fueled.android.core.common.contract.SideEffect
import com.ratik.uttam.core.MessageState

sealed class ViewEvent {
  data class Navigate(val target: NavigationTarget) : ViewEvent()

  data class DisplayMessage(val message: MessageState) : ViewEvent()

  data class Effect(val effect: SideEffect) : ViewEvent()
}
