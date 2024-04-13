package com.ratik.uttam.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ratik.uttam.ui.home.HomeScreen
import com.ratik.uttam.ui.theme.UttamTheme

@Composable
internal fun MainApp() {
    UttamTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            HomeScreen()
        }
    }
}
