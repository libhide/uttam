package com.ratik.uttam.ui.landing

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.SpaceAround
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.SpaceEvenly
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ratik.uttam.R
import com.ratik.uttam.ui.theme.Dimens
import com.ratik.uttam.ui.theme.Dimens.LandingScreenLogoTopPadding
import com.ratik.uttam.ui.theme.Dimens.LandingScreenLogoWidth
import com.ratik.uttam.ui.theme.Dimens.SpacingNormal
import com.ratik.uttam.ui.theme.Dimens.SpacingXLarge
import com.ratik.uttam.ui.theme.Dimens.SpacingXXXLarge
import com.ratik.uttam.ui.theme.Dimens.SpacingXXXXLarge
import com.ratik.uttam.ui.theme.Dimens.SpacingXXXXXLarge

@Composable
internal fun LandingScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.uttam_hero_splash),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = Crop,
        )

        Column(
            verticalArrangement = SpaceBetween,
            horizontalAlignment = CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .padding(
                    horizontal = SpacingNormal,
                    vertical = SpacingXXXXXLarge,
                ),
        ) {
            Image(
                painter = painterResource(id = R.drawable.ty_logo),
                contentDescription = stringResource(id = R.string.content_desc_app_logo),
                modifier = Modifier
                    .width(LandingScreenLogoWidth)
                    .padding(top = LandingScreenLogoTopPadding),
            )

            Button(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = SpacingXLarge),
            ) {
                Text(text = stringResource(id = R.string.get_started_text))
            }
        }
    }
}