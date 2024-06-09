package com.ratik.uttam.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color.Companion.Transparent
import com.ratik.uttam.ui.theme.ColorPrimary
import com.ratik.uttam.ui.theme.TextColor

@Composable
fun AppBar(
  title: @Composable () -> Unit,
  actions: @Composable RowScope.() -> Unit = {},
  isTransparent: Boolean,
) {
  TopAppBar(
    title = title,
    actions = actions,
    backgroundColor = if (isTransparent) Transparent else ColorPrimary,
    contentColor = TextColor,
  )
}
