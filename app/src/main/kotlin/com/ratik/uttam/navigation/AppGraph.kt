package com.ratik.uttam.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.ratik.uttam.navigation.model.Destination
import com.ratik.uttam.navigation.model.Graph
import com.ratik.uttam.ui.home.HomeScreen
import com.ratik.uttam.ui.landing.LandingScreen

object AppGraph : Graph("uttam")

sealed class AppDestination {

    object Landing : Destination("landing")

    object Home : Destination("home")
}

fun NavGraphBuilder.addLandingScreen(
    graph: Graph,
    navigateToHome: () -> Unit,
) {
    composable(
        route = AppDestination.Landing.createRoute(graph),
    ) {
        LandingScreen(
            navigateToHome = navigateToHome,
        )
    }
}

fun NavGraphBuilder.addHomeScreen(
    graph: Graph,
) {
    composable(
        route = AppDestination.Home.createRoute(graph),
    ) {
        HomeScreen()
    }
}