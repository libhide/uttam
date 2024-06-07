package com.ratik.uttam.ui.components

import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color.Companion.White
import com.ratik.uttam.ui.components.UttamButton.Primary
import com.ratik.uttam.ui.theme.Dimens.ALPHA_70
import com.ratik.uttam.ui.theme.ColorPrimary
import com.ratik.uttam.ui.theme.ColorPrimaryVariant

@Composable
fun ButtonDefaults.themedDefaults(style: UttamButton): ButtonColors {
    return when (style) {
        Primary -> buttonColors(
            backgroundColor = White,
            contentColor = ColorPrimaryVariant,
            disabledBackgroundColor = White.copy(alpha = ALPHA_70),
            disabledContentColor = ColorPrimaryVariant.copy(alpha = ALPHA_70),
        )
    }
}
