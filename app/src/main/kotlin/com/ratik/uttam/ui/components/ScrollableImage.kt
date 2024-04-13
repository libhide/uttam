package com.ratik.uttam.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale

@Composable
internal fun ScrollableImage(
    modifier: Modifier = Modifier, painter: Painter,
    contentDescription: String?,
) {
    Image(
        modifier = modifier
            .horizontalScroll(rememberScrollState()),
        painter = painter,
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
    )
}