package com.ratik.uttam.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.ratik.uttam.R
import com.ratik.uttam.ui.components.ScrollableImage
import com.ratik.uttam.ui.theme.Dimens.SpacingNormal
import com.ratik.uttam.ui.theme.Dimens.TextSizeXXLarge
import com.ratik.uttam.ui.theme.UttamTheme

@Composable
internal fun MainApp() {
    val uttamHero = painterResource(id = R.drawable.uttam_hero)

    UttamTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
        ) {
            ScrollableImage(
                painter = uttamHero,
                contentDescription = null,
            )
            Column(
                modifier = Modifier
                    .safeDrawingPadding()
                    .padding(horizontal = SpacingNormal),
            ) {
                Text(
                    text = "Uttam",
                    color = Color.White,
                    fontSize = TextSizeXXLarge,
                )
            }
        }
    }
}
