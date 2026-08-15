package com.ratik.uttam.data.extensions

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow

/**
 * Implements callbacks for stages of flow collection that allows for maximising unit test code
 * coverage.
 */
suspend fun <T : Any> Flow<T>.collectBy(
  onStart: () -> Unit = {},
  onEach: suspend (T) -> Unit = { _ -> },
  onError: (Throwable) -> Unit = { _ -> },
) {
  try {
    onStart()
    collect { item -> onEach(item) }
  } catch (exception: CancellationException) {
    throw exception
  } catch (exception: Exception) {
    onError(exception)
  }
}
