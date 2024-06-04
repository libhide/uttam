package com.ratik.uttam.ui.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Composable
fun VerticalSpacer(size: Dp) {
    Spacer(modifier = Modifier.height(size))
}

@Composable
fun RowScope.VerticalSpacer(weight: Float) {
    Spacer(modifier = Modifier.weight(weight))
}

@Composable
fun ColumnScope.VerticalSpacer(weight: Float) {
    Spacer(modifier = Modifier.weight(weight))
}

@Composable
fun HorizontalSpacer(size: Dp) {
    Spacer(modifier = Modifier.width(size))
}

@Composable
fun RowScope.HorizontalSpacer(size: Dp) {
    Spacer(modifier = Modifier.width(size))
}

@Composable
fun RowScope.HorizontalSpacer(weight: Float) {
    Spacer(modifier = Modifier.weight(weight))
}

@Composable
fun ColumnScope.HorizontalSpacer(weight: Float) {
    Spacer(modifier = Modifier.weight(weight))
}