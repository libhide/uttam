package com.ratik.uttam.ui.theme

import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White

val ColorPrimary = Color(color = 0xFF838383)
val ColorPrimaryVariant = Color(color = 0xFF646464)
val TextColor = White

val ProjectLightColors =
  lightColors(
    primary = ColorPrimary,
    onPrimary = White,
    primaryVariant = ColorPrimaryVariant,
    secondary = ColorPrimary,
    secondaryVariant = ColorPrimaryVariant,
    onSecondary = White,
    onBackground = TextColor,
    surface = White,
    onSurface = ColorPrimaryVariant,
  )

val ProjectDarkColors =
  darkColors(
    primary = ColorPrimary,
    primaryVariant = ColorPrimaryVariant,
    onPrimary = White,
    onSecondary = White,
  )

// Global
val ShimmerColorLight = Color(color = 0xFFF0F2F5)
val ShimmerColorMedium = Color(color = 0xFFDEE2E9)

// Onboarding
val OnboardingBackground = Color(color = 0xFF3D3D3D)
