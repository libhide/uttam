package com.ratik.uttam.ui.theme

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color.TRANSPARENT
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun UttamTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
  val colorScheme =
    when {
      darkTheme -> ProjectLightColors // TODO: implement dark mode colours
      else -> ProjectLightColors
    }
  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as Activity).window
      window.statusBarColor = TRANSPARENT
      WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
    }
  }

  MaterialTheme(
    colors = colorScheme,
    typography = UttamTypography,
    shapes = UttamShapes,
    content = content,
  )
}

@SuppressLint("ComposableNaming")
@Composable
fun setStatusBarColors(color: Color = Transparent, isDarkIcons: Boolean) {
  val uiController = rememberSystemUiController()
  SideEffect { uiController.setStatusBarColor(color = color, darkIcons = isDarkIcons) }
}

@Composable
fun setNavigationBarColors(isDarkIcons: Boolean, backgroundColor: Color) {
  val uiController = rememberSystemUiController()
  SideEffect {
    uiController.setNavigationBarColor(color = backgroundColor, darkIcons = isDarkIcons)
  }
}

object UttamTheme {
  val colors: Colors
    @Composable @ReadOnlyComposable
    get() = MaterialTheme.colors

  val typography: Typography
    @Composable @ReadOnlyComposable
    get() = UttamTypography

  val shapes: Shapes
    @Composable @ReadOnlyComposable
    get() = UttamShapes
}
