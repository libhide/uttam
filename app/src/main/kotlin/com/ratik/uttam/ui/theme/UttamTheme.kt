package com.ratik.uttam.ui.theme

import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView

@Composable
fun UttamTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
  val colorScheme =
    when {
      darkTheme -> ProjectLightColors // TODO: implement dark mode colours
      else -> ProjectLightColors
    }
  SetSystemBarColors(statusBarDarkIcons = darkTheme)

  MaterialTheme(
    colors = colorScheme,
    typography = UttamTypography,
    shapes = UttamShapes,
    content = content,
  )
}

@Composable
fun SetSystemBarColors(
  statusBarColor: Color = Transparent,
  statusBarDarkIcons: Boolean,
  navigationBarColor: Color? = null,
  navigationBarDarkIcons: Boolean? = null,
) {
  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val activity = view.context as ComponentActivity
      activity.enableEdgeToEdge(
        statusBarStyle = statusBarColor.toSystemBarStyle(statusBarDarkIcons),
        navigationBarStyle =
        if (navigationBarColor != null && navigationBarDarkIcons != null) {
          navigationBarColor.toSystemBarStyle(navigationBarDarkIcons)
        } else {
          SystemBarStyle.auto(Transparent.toArgb(), Transparent.toArgb())
        },
      )
    }
  }
}

private fun Color.toSystemBarStyle(darkIcons: Boolean): SystemBarStyle =
  if (darkIcons) {
    SystemBarStyle.light(toArgb(), toArgb())
  } else {
    SystemBarStyle.dark(toArgb())
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
