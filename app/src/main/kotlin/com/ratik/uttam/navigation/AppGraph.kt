package com.ratik.uttam.navigation

import android.window.SplashScreen
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.ratik.uttam.navigation.model.Destination
import com.ratik.uttam.navigation.model.Graph
import com.ratik.uttam.ui.home.HomeScreen
import com.ratik.uttam.ui.landing.LandingScreen
import com.ratik.uttam.ui.onboarding.OnboardingScreen
import com.ratik.uttam.ui.splash.SplashScreen

object AppGraph : Graph("uttam")

sealed class AppDestination {
    object Splash : Destination("splash")

    object Landing : Destination("landing")

    object Onboarding : Destination("onboarding")

    object Home : Destination("home")
}

fun NavGraphBuilder.addSplashScreen(
    graph: Graph,
    navigateToHome: () -> Unit,
    navigateToLanding: () -> Unit,
) {
    composable(
        route = AppDestination.Splash.createRoute(graph),
    ) {
        SplashScreen(
            navigateToHome = navigateToHome,
            navigateToLanding = navigateToLanding,
        )
    }
}

fun NavGraphBuilder.addLandingScreen(
    graph: Graph,
    navigateToOnboarding: () -> Unit,
) {
    composable(
        route = AppDestination.Landing.createRoute(graph),
    ) {
        LandingScreen(
            navigateToOnboarding = navigateToOnboarding,
        )
    }
}

fun NavGraphBuilder.addOnboardingScreen(
    graph: Graph,
    navigateToHome: () -> Unit,
) {
    composable(
        route = AppDestination.Onboarding.createRoute(graph),
    ) {
        OnboardingScreen(
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