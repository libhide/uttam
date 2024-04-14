package com.ratik.uttam.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
internal fun ScrollableImage(
    modifier: Modifier = Modifier,
    model: ImageRequest,
    contentDescription: String?,
) {
    AsyncImage(
        modifier = modifier
            .horizontalScroll(rememberScrollState()),
        model = model,
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
    )
}