package com.ratik.uttam.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.style.LineHeightStyle
import com.ratik.uttam.ui.theme.Dimens.TextSizeDefault
import com.ratik.uttam.ui.theme.Dimens.TextSizeSmall
import com.ratik.uttam.ui.theme.Dimens.TextSizeXXLarge

val UttamTypography =
  Typography(
    h1 =
    TextStyle(
      fontFamily = FontFamily.Default,
      fontWeight = FontWeight.Bold,
      fontSize = TextSizeXXLarge,
    )
      .trimPadding(),
    body1 =
    TextStyle(
      fontFamily = FontFamily.Default,
      fontWeight = FontWeight.Normal,
      fontSize = TextSizeDefault,
    )
      .trimPadding(),
    button =
    TextStyle(fontWeight = SemiBold, fontSize = TextSizeSmall, color = ColorPrimary)
      .trimPadding(),
  )

fun TextStyle.trimPadding(): TextStyle {
  return this.copy(
    lineHeightStyle =
    LineHeightStyle(
      alignment = LineHeightStyle.Alignment.Center,
      trim = LineHeightStyle.Trim.Both,
    ),
    platformStyle = PlatformTextStyle(includeFontPadding = false),
  )
}
