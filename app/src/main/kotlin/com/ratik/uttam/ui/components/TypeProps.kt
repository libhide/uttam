package com.ratik.uttam.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.Normal
import androidx.compose.ui.unit.TextUnit
import com.ratik.uttam.ui.components.UttamText.AppBar
import com.ratik.uttam.ui.components.UttamText.Body
import com.ratik.uttam.ui.components.UttamText.BodyBig
import com.ratik.uttam.ui.components.UttamText.BodyBold
import com.ratik.uttam.ui.components.UttamText.BodySmall
import com.ratik.uttam.ui.components.UttamText.Caption
import com.ratik.uttam.ui.components.UttamText.CaptionBold
import com.ratik.uttam.ui.theme.Dimens.TextSizeDefault
import com.ratik.uttam.ui.theme.Dimens.TextSizeLarge
import com.ratik.uttam.ui.theme.Dimens.TextSizeSmall
import com.ratik.uttam.ui.theme.Dimens.TextSizeXLarge
import com.ratik.uttam.ui.theme.Dimens.TextSizeXSmall

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

    BodyBig -> TypeProps(
        fontWeight = Normal,
        textSize = TextSizeLarge,
    )

    BodySmall -> TypeProps(
        fontWeight = Normal,
        textSize = TextSizeSmall,
    )

    BodyBold -> TypeProps(
        fontWeight = Bold,
        textSize = TextSizeDefault,
    )

    AppBar -> TypeProps(
        fontWeight = Normal,
        textSize = TextSizeXLarge,
    )

    Caption -> TypeProps(
        fontWeight = Normal,
        textSize = TextSizeSmall,
    )

    CaptionBold -> TypeProps(
        fontWeight = Bold,
        textSize = TextSizeSmall,
    )
}
