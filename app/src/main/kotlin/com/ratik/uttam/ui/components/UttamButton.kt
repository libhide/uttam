package com.ratik.uttam.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.ratik.uttam.ui.theme.ColorPrimaryVariant
import com.ratik.uttam.ui.theme.Dimens.ButtonHeight
import com.ratik.uttam.ui.theme.Dimens.PERCENT_50
import com.ratik.uttam.ui.theme.Dimens.ProgressIndicatorSize
import com.ratik.uttam.ui.theme.Dimens.SpacingNormal
import com.ratik.uttam.ui.theme.Dimens.SpacingXXXSmall
import com.ratik.uttam.ui.theme.UttamTheme

/**
 * Core Button UI components. Renders the correct button style based on the enum type declared.
 *
 * Can be used like the following:
 * ```
 * UttamButton.Primary(
 *     text = "Hello, World!",
 *     onClick = ::performSomeAction,
 * )
 * ```
 */
enum class UttamButton {
  Primary;

  /**
   * @param onClick - Is not invoked when [isLoading] is true to prevent clicking on loading buttons
   */
  @Composable
  operator fun invoke(
    modifier: Modifier = Modifier,
    text: String,
    isEnabled: Boolean = true,
    isLoading: Boolean = false,
    @DrawableRes leadingIcon: Int? = null,
    onClick: () -> Unit,
  ) {
    val foregroundColor =
      when (this) {
        Primary -> ColorPrimaryVariant
      }
    val border = null

    Button(
      modifier = Modifier.height(ButtonHeight).then(modifier),
      border = border,
      colors = ButtonDefaults.themedDefaults(style = this),
      onClick = { if (!isLoading) onClick() },
      enabled = isEnabled,
      content = {
        AnimatedVisibility(visible = !isLoading) {
          Row {
            if (leadingIcon != null) {
              Icon(
                painter = painterResource(id = leadingIcon),
                contentDescription = null,
                tint = if (isEnabled) foregroundColor else foregroundColor.copy(alpha = PERCENT_50),
              )
              HorizontalSpacer(size = SpacingXXXSmall)
            }
            Text(
              style = UttamTheme.typography.button,
              text = text.uppercase(),
              color = if (isEnabled) foregroundColor else foregroundColor.copy(alpha = PERCENT_50),
            )
          }
        }
        AnimatedVisibility(visible = isLoading) {
          CircularProgressIndicator(
            modifier = Modifier.size(ProgressIndicatorSize),
            color = foregroundColor,
          )
        }
      },
    )
  }
}

@Preview
@Composable
private fun UttamButton_Primary_Preview() {
  Column {
    UttamButton.Primary(
      modifier = Modifier.fillMaxWidth(),
      text = BUTTON_TEXT,
      isEnabled = true,
      isLoading = false,
      onClick = {},
    )
    VerticalSpacer(SpacingNormal)
    UttamButton.Primary(
      modifier = Modifier.fillMaxWidth(),
      text = BUTTON_TEXT,
      isEnabled = true,
      isLoading = true,
      onClick = {},
    )
  }
}

private const val BUTTON_TEXT = "Hello World"
