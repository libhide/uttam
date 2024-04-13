package com.ratik.uttam.ui.home

import com.ratik.uttam.core.MessageState
import com.ratik.uttam.core.contract.BaseState

internal data class HomeState(
    override val isLoading: Boolean = false,
    override val errorState: MessageState? = null,
) : BaseState {
    companion object {
        val initialState = HomeState()
    }
}