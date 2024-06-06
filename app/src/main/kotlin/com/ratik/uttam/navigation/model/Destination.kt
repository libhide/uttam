package com.ratik.uttam.navigation.model

import com.ratik.uttam.core.SafeAbstractClass

/**
 * A [Destination] represents a single screen within the app. [Destination]s are independent
 * of their parent [Graph] and can be added to multiple [Graph]s within the app. The [createRoute]
 * function can be used to generate a unique route for that given [Destination] on a specific
 * [Graph]
 *
 * Consider the following example:
 * Home Tab -> Products List Screen -> Product Detail Screen
 * Cart Tab -> Shopping Cart Screen -> Product Detail Screen
 *
 * The Home [Graph] would have the following two destinations:
 * ```
 * object HomeGraph : Graph() { route = "home" }
 *
 * object ProductList : Destination() { route = "product_list" }
 * object ProductDetail : Destination() { route = "product_detail/{id}" }
 * ```
 * The products module would expose a function which adds each destination to a specific graph:
 * ```
 * fun NavGraphBuilder.addProductList(graph: Graph) {
 *     composable(
 *         route = ProductList.createRoute(graph),
 *     ) {
 *         ProductListScreen()
 *     }
 * }
 *
 * fun NavGraphBuilder.addProductDetail(graph: Graph) {
 *     composable(
 *         route = ProductDetail.createRoute(graph),
 *     ) {
 *         ProductDetailScreen()
 *     }
 * }
 * ```
 * The Cart [Graph] would have then only one destination
 * (as we can reuse the existing destination define in the home module):
 * ```
 * object CartGraph : Graph() { route = "cart" }
 *
 * object ShoppingCart : Destination() { route = "shopping_cart" }
 * ```
 * The cart module would expose a function which adds the destination to any specified graph:
 * ```
 * fun NavGraphBuilder.addShoppingCart(graph: Graph) {
 *     composable(
 *         route = ShoppingCart.createRoute(graph),
 *     ) {
 *         ShoppingCartScreen()
 *     }
 * }
 * ```
 *
 * When constructing the navigation for these two graphs we can add any screen to any graph,
 * by simply passing the [Graph] into the functions define above:
 *
 * ```
 * private fun NavGraphBuilder.createHomeGraph() {
 *     navigation(
 *         route = HomeGraph.route,
 *         startDestination = ProductList.createRoute(HomeGraph),
 *     ) {
 *         addProductList(graph = HomeGraph)
 *         addProductDetail(graph = HomeGraph)
 *     }
 * }
 *
 * private fun NavGraphBuilder.createCartGraph() {
 *     navigation(
 *         route = CartGraph.route,
 *         startDestination = ShoppingCart.createRoute(CartGraph),
 *     ) {
 *         addShoppingCart(graph = CartGraph)
 *         addProductDetail(graph = CartGraph)
 *     }
 * }
 */
@SafeAbstractClass
abstract class Destination(private val route: String) {

    fun createRoute(graph: Graph): String {
        return "${graph.route}/$route"
    }
}
