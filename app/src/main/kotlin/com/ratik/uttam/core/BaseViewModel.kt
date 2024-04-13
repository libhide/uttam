package com.ratik.uttam.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ratik.uttam.core.contract.BaseState
import com.fueled.android.core.common.contract.ViewEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow.SUSPEND
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * This is the common part of all ViewModels, dealing with ViewState and SideEffect propagation.
 */
abstract class BaseViewModel<ViewState : BaseState, Action>(
    initialState: ViewState,
    protected val dispatcherProvider: DispatcherProvider,
) : ViewModel() {

    @Inject
    lateinit var resourceProvider: StringProvider

    private val _state: MutableStateFlow<ViewState> = MutableStateFlow(initialState)
    val state: StateFlow<ViewState> = _state.asStateFlow()

    private val _events = Channel<ViewEvent>(
        capacity = Int.MAX_VALUE,
        onBufferOverflow = SUSPEND,
        onUndeliveredElement = {
            Timber.e(IllegalStateException("Missed view event $it"))
        },
    )
    val events
        get() = _events.receiveAsFlow()

    protected val currentState: ViewState
        get() = state.value

    abstract fun onViewAction(viewAction: Action)

    abstract fun handleError(throwable: Throwable)

    protected fun dispatchViewEvent(event: ViewEvent) {
        launch {
            _events.send(event)
        }
    }

    protected fun launch(
        block: suspend CoroutineScope.() -> Unit,
    ): Job {
        return viewModelScope.launch(context = dispatcherProvider.main, block = block)
    }

    /**
     * Updates the [currentState] with the value returned from the [transform] lambda
     */
    protected fun updateState(transform: (ViewState) -> ViewState) {
        _state.update(transform)
    }
}
