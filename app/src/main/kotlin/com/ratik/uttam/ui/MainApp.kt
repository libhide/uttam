package com.ratik.uttam.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
internal fun MainApp() {
    Box(
        modifier = Modifier
            .size(150.dp)
            .background(Color.Blue),
    )
}
