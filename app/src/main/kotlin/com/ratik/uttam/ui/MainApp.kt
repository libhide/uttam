package com.ratik.uttam.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.ratik.uttam.navigation.MainNavHost
import com.ratik.uttam.ui.theme.UttamTheme

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
internal fun MainApp() {
    val navController = rememberNavController()
    var toolbarText by rememberSaveable { mutableStateOf("") }

    UttamTheme {
        Scaffold { _ ->
            MainNavHost(
                navController = navController,
                setToolbarTitle = { title -> toolbarText = title },
            )
        }
    }
}
