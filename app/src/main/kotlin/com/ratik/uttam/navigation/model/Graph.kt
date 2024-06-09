package com.ratik.uttam.navigation.model

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import com.ratik.uttam.core.SafeAbstractClass

/**
 * A [Graph] represents a nested Navigation-Graph that can defined within the app. Some apps can
 * have multiple nested navigation graphs; for example in the case of a bottom navigation and each
 * tab should retain its own back stack.
 *
 * By defining a [Graph] for each bottom navigation destination, we can then define a set of
 * [Destination]s within that [Graph] that can be navigated to.
 *
 * In the case of no bottom navigation, or not wanting to retain a back stack of each tabs
 * navigation hierarchy, we create a single [Graph] and simply define a set of all the
 * [Destination]s on that single [Graph]
 *
 * @property route The unique path for this particular graph. (For example: 'shop' for the shop
 *   tab.)
 */
@SafeAbstractClass abstract class Graph(val route: String)

object GraphSaver : Saver<Graph, String> {
  override fun restore(value: String): Graph = object : Graph(value) {}

  override fun SaverScope.save(value: Graph): String = value.route
}
