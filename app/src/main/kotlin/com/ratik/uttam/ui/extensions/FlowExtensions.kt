package com.ratik.uttam.ui.extensions

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@Composable
fun <T> rememberFlowOnLifecycle(
  flow: Flow<T>,
  lifecycle: Lifecycle = LocalLifecycleOwner.current.lifecycle,
  minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
): Flow<T> =
  remember(flow, lifecycle) {
    flow.flowWithLifecycle(lifecycle = lifecycle, minActiveState = minActiveState)
  }

/**
 * Create an effect that matches the lifecycle of the call site. If LandingScreen recomposes, the
 * delay shouldn't start again.
 *
 * https://stackoverflow.com/a/71626121/6510726
 */
@SuppressLint("ComposableNaming")
@Composable
fun <T> Flow<T>.collectAsEffect(
  context: CoroutineContext = EmptyCoroutineContext,
  block: suspend (T) -> Unit,
) {
  LaunchedEffect(key1 = Unit) { onEach(block).flowOn(context).launchIn(this) }
}
