package com.ratik.uttam.ui.components

import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color.Companion.White
import com.ratik.uttam.ui.components.UttamButton.Primary
import com.ratik.uttam.ui.theme.Dimens.ALPHA_70
import com.ratik.uttam.ui.theme.ColorPrimary

@Composable
fun ButtonDefaults.themedDefaults(style: UttamButton): ButtonColors {
    return when (style) {
        Primary -> buttonColors(
            backgroundColor = White,
            contentColor = ColorPrimary,
            disabledBackgroundColor = White.copy(alpha = ALPHA_70),
            disabledContentColor = ColorPrimary.copy(alpha = ALPHA_70),
        )
    }
}
