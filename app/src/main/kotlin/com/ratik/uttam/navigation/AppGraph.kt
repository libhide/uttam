package com.ratik.uttam.navigation

import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.ratik.uttam.navigation.model.Destination
import com.ratik.uttam.navigation.model.Graph
import com.ratik.uttam.ui.feature.home.HomeScreen
import com.ratik.uttam.ui.feature.landing.LandingScreen
import com.ratik.uttam.ui.feature.onboarding.OnboardingScreen
import com.ratik.uttam.ui.feature.settings.SettingsScreen
import com.ratik.uttam.ui.feature.splash.SplashScreen

object AppGraph : Graph("uttam")

sealed class AppDestination {
  object Splash : Destination("splash")

  object Landing : Destination("landing")

  object Onboarding : Destination("onboarding")

  object Home : Destination("home")

  object Settings : Destination("settings")
}

fun NavGraphBuilder.addSplashScreen(
  graph: Graph,
  navigateToHome: () -> Unit,
  navigateToLanding: () -> Unit,
) {
  composable(route = AppDestination.Splash.createRoute(graph)) {
    SplashScreen(navigateToHome = navigateToHome, navigateToLanding = navigateToLanding)
  }
}

fun NavGraphBuilder.addLandingScreen(graph: Graph, navigateToOnboarding: () -> Unit) {
  composable(route = AppDestination.Landing.createRoute(graph)) {
    LandingScreen(navigateToOnboarding = navigateToOnboarding)
  }
}

fun NavGraphBuilder.addOnboardingScreen(graph: Graph, navigateToHome: () -> Unit) {
  composable(route = AppDestination.Onboarding.createRoute(graph)) {
    OnboardingScreen(navigateToHome = navigateToHome)
  }
}

fun NavGraphBuilder.addHomeScreen(graph: Graph, navigateToSettings: () -> Unit) {
  composable(
    route = AppDestination.Home.createRoute(graph),
    exitTransition = {
      slideOutHorizontally(
        animationSpec = tween(NAVIGATION_TRANSITION_DURATION),
        targetOffsetX = { -it / BACKGROUND_SCREEN_OFFSET_DIVISOR },
      ) + fadeOut(tween(NAVIGATION_TRANSITION_DURATION))
    },
    popEnterTransition = {
      slideInHorizontally(
        animationSpec = tween(NAVIGATION_TRANSITION_DURATION),
        initialOffsetX = { -it / BACKGROUND_SCREEN_OFFSET_DIVISOR },
      ) + fadeIn(tween(NAVIGATION_TRANSITION_DURATION))
    },
  ) {
    HomeScreen(navigateToSettings = navigateToSettings)
  }
}

fun NavGraphBuilder.addSettingsScreen(graph: Graph, navigateUp: () -> Unit) {
  composable(
    route = AppDestination.Settings.createRoute(graph),
    enterTransition = {
      slideIntoContainer(
        towards = SlideDirection.Left,
        animationSpec = tween(NAVIGATION_TRANSITION_DURATION),
      ) + fadeIn(tween(NAVIGATION_TRANSITION_DURATION))
    },
    popExitTransition = {
      slideOutOfContainer(
        towards = SlideDirection.Right,
        animationSpec = tween(NAVIGATION_TRANSITION_DURATION),
      ) + fadeOut(tween(NAVIGATION_TRANSITION_DURATION))
    },
  ) {
    SettingsScreen(navigateUp = navigateUp)
  }
}

private const val NAVIGATION_TRANSITION_DURATION = 300
private const val BACKGROUND_SCREEN_OFFSET_DIVISOR = 4
