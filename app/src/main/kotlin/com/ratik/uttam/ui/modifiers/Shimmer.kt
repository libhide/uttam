package com.ratik.uttam.ui.modifiers

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TileMode
import com.ratik.uttam.ui.theme.ShimmerColorLight
import com.ratik.uttam.ui.theme.ShimmerColorMedium

fun Modifier.shimmerBackground(
    shape: Shape = RectangleShape,
    shimmerColors: List<Color> = listOf(
        ShimmerColorLight,
        ShimmerColorMedium,
    ),
): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "")
    val translateAnimation by transition.animateFloat(
        initialValue = 0F,
        targetValue = 400F,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = ANIM_DURATION, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "",
    )
    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnimation, translateAnimation),
        end = Offset(translateAnimation + GRADIENT_OFFSET, translateAnimation + GRADIENT_OFFSET),
        tileMode = TileMode.Mirror,
    )
    return@composed this.then(background(brush = brush, shape = shape))
}

private const val ANIM_DURATION = 1500
private const val GRADIENT_OFFSET = 250F