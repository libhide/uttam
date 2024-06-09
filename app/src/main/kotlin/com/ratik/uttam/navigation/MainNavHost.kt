package com.ratik.uttam.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.navigation
import com.ratik.uttam.navigation.model.Graph
import com.ratik.uttam.navigation.model.GraphSaver

/**
 * This function sets up the main navigation graph. As we have a bottom navigation bar, this sets
 * different navigation graphs for all the bottom items.
 *
 * Based on the current business logic (having a bottom navigation bar) we set a sub-graph for each
 * bottom navigation item, so in order to preserve a back stack for each bottom navigation item.
 *
 * Depending on different business requirements (such as different navigation type of not having a
 * bottom navigation at all) it can happen that there is no need for this level of complexity and
 * everything can be added to a single navigation graph.
 */
@Composable
internal fun MainNavHost(modifier: Modifier = Modifier, navController: NavHostController) {
  val rootGraph by rememberSaveable(stateSaver = GraphSaver) { mutableStateOf(AppGraph) }

  NavHost(navController = navController, modifier = modifier, startDestination = rootGraph.route) {
    addAppGraph(navController = navController)
  }
}

private fun NavGraphBuilder.addAppGraph(graph: Graph = AppGraph, navController: NavHostController) {
  navigation(route = graph.route, startDestination = AppDestination.Splash.createRoute(graph)) {
    addSplashScreen(
      graph = graph,
      navigateToHome = {
        navController.navigate(AppDestination.Home.createRoute(graph)) {
          popUpTo(AppDestination.Splash.createRoute(graph)) { inclusive = true }
        }
      },
      navigateToLanding = {
        navController.navigate(AppDestination.Landing.createRoute(graph)) {
          popUpTo(AppDestination.Splash.createRoute(graph)) { inclusive = true }
        }
      },
    )
    addLandingScreen(
      graph = graph,
      navigateToOnboarding = {
        navController.navigate(AppDestination.Onboarding.createRoute(graph))
      },
    )
    addOnboardingScreen(
      graph = graph,
      navigateToHome = {
        navController.navigate(AppDestination.Home.createRoute(graph)) {
          popUpTo(AppDestination.Landing.createRoute(graph)) { inclusive = true }
        }
      },
    )
    addHomeScreen(
      graph = graph,
      navigateToSettings = { navController.navigate(AppDestination.Settings.createRoute(graph)) },
    )
    addSettingsScreen(graph = graph, navigateUp = navController::navigateUp)
  }
}
