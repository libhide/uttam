package com.ratik.uttam.data.extensions

import kotlinx.coroutines.flow.Flow

/**
 * Implements callbacks for stages of flow collection that allows for maximising unit test
 * code coverage.
 */
suspend inline fun <T : Any> Flow<T>.collectBy(
    onStart: () -> Unit = {},
    crossinline onEach: (T) -> Unit = { _ -> },
    onError: (Throwable) -> Unit = { _ -> },
) {
    try {
        onStart()
        collect { item -> onEach(item) }
    } catch (e: Exception) {
        onError(e)
    }
}
