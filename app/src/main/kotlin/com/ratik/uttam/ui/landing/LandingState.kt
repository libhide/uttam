package com.ratik.uttam.ui.landing

import com.ratik.uttam.core.MessageState
import com.ratik.uttam.core.contract.BaseState

internal data class LandingState(
    override val isLoading: Boolean = false,
    override val errorState: MessageState? = null,
) : BaseState {
    companion object {
        val initialState = LandingState()
    }
}