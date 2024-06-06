package com.ratik.uttam.core

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

/**
 * Used to inject DispatcherProvider into business logic code, wherever we need to define the dispatcher...
 */
open class DispatcherProvider @Inject constructor() {
    open val main: CoroutineDispatcher = Dispatchers.Main
    open val default: CoroutineDispatcher = Dispatchers.Default
    open val io: CoroutineDispatcher = Dispatchers.IO
}
