package com.ratik.uttam.ui

import android.annotation.SuppressLint
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.ratik.uttam.navigation.MainNavHost
import com.ratik.uttam.ui.theme.UttamTheme

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
internal fun MainApp() {
  val navController = rememberNavController()

  UttamTheme { Scaffold { _ -> MainNavHost(navController = navController) } }
}
