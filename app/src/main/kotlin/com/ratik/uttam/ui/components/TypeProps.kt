package com.ratik.uttam.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.Normal
import androidx.compose.ui.unit.TextUnit
import com.ratik.uttam.ui.components.UttamText.AppBar
import com.ratik.uttam.ui.components.UttamText.Body
import com.ratik.uttam.ui.components.UttamText.BodyBold
import com.ratik.uttam.ui.theme.Dimens.TextSizeDefault
import com.ratik.uttam.ui.theme.Dimens.TextSizeXLarge

internal data class TypeProps(
    val fontWeight: FontWeight,
    val textSize: TextUnit,
)

@Composable
internal fun getTypeProps(text: UttamText): TypeProps = when (text) {
    Body -> TypeProps(
        fontWeight = Normal,
        textSize = TextSizeDefault,
    )

    BodyBold -> TypeProps(
        fontWeight = Bold,
        textSize = TextSizeDefault,
    )

    AppBar -> TypeProps(
        fontWeight = Normal,
        textSize = TextSizeXLarge,
    )
}
