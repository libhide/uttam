package com.ratik.uttam.ui.components

import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import com.ratik.uttam.ui.theme.TextColor

/**
 * Core Text UI components across the six flags application. Renders the correct text style based on
 * the enum type declared.
 *
 * Can be used like the following:
 * ```
 * UttamText.H4(text = "Hello, World!")
 * UttamText.Body(text = "Hello, World!")
 * ```
 */
enum class UttamText {
  Body,
  BodySmall,
  BodyBig,
  BodyBold,
  Caption,
  CaptionBold,
  AppBar,
  ;

  @Composable
  operator fun invoke(
    modifier: Modifier = Modifier,
    text: String,
    annotatedText: AnnotatedString? = null,
    textAlign: TextAlign = TextAlign.Start,
    textColor: Color = TextColor,
    textSize: TextUnit? = null,
    maxLines: Int = Int.MAX_VALUE,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    textDecoration: TextDecoration = TextDecoration.None,
  ) {
    val typeProps = getTypeProps(this)
    val textStyle = LocalTextStyle.current
    if (annotatedText != null) {
      Text(
        modifier = modifier,
        text = annotatedText,
        textAlign = textAlign,
        color = textColor,
        maxLines = maxLines,
        fontSize = textSize ?: typeProps.textSize,
        fontWeight = typeProps.fontWeight,
        letterSpacing = letterSpacing,
        overflow = TextOverflow.Ellipsis,
        onTextLayout = onTextLayout,
        style = textStyle.copy(textDecoration = textDecoration),
      )
    } else {
      Text(
        modifier = modifier,
        text = text,
        textAlign = textAlign,
        color = textColor,
        maxLines = maxLines,
        fontSize = textSize ?: typeProps.textSize,
        fontWeight = typeProps.fontWeight,
        letterSpacing = letterSpacing,
        overflow = TextOverflow.Ellipsis,
        onTextLayout = onTextLayout,
        style = textStyle.copy(textDecoration = textDecoration),
      )
    }
  }
}
