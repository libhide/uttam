package com.ratik.uttam.ui.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.fueled.android.core.ui.extensions.collectAsEffect
import com.ratik.uttam.R
import com.ratik.uttam.core.Ignored
import com.ratik.uttam.core.contract.ViewEvent.Navigate
import com.ratik.uttam.ui.splash.SplashNavTarget.Home
import com.ratik.uttam.ui.splash.SplashNavTarget.Landing
import com.ratik.uttam.ui.theme.ColorPrimary
import com.ratik.uttam.ui.theme.ColorPrimaryVariant

@Composable
internal fun SplashScreen(
    viewModel: SplashViewModel = hiltViewModel(),
    navigateToHome: () -> Unit,
    navigateToLanding: () -> Unit,
) {
    viewModel.events.collectAsEffect { event ->
        when (event) {
            is Navigate -> {
                when (event.target) {
                    is Home -> navigateToHome()
                    is Landing -> navigateToLanding()
                }
            }

            else -> Ignored
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorPrimary)
    ) {
        Image(
            painter = painterResource(id = R.drawable.splash_logo),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center)
        )
    }
}