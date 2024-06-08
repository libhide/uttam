package com.ratik.uttam.ui.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ratik.uttam.R
import com.ratik.uttam.ui.components.UttamText
import com.ratik.uttam.ui.theme.ColorPrimaryVariant
import com.ratik.uttam.ui.theme.setNavigationBarColors
import com.ratik.uttam.ui.theme.setStatusBarColors

@Composable
fun SettingsScreen(
    navigateUp: () -> Unit,
) {
    setStatusBarColors(
        isDarkIcons = false,
        color = ColorPrimaryVariant
    )
    setNavigationBarColors(
        isDarkIcons = false,
        backgroundColor = ColorPrimaryVariant
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        SettingsAppBar(
            modifier = Modifier
                .fillMaxWidth()
                .systemBarsPadding()
                .align(TopCenter),
            navigateUp = navigateUp
        )
    }
}

@Composable
private fun SettingsAppBar(
    modifier: Modifier = Modifier,
    navigateUp: () -> Unit,
) {
    TopAppBar(
        modifier = modifier,
        elevation = 0.dp,
        title = {
            UttamText.AppBar(
                text = stringResource(id = R.string.settings_label),
                textColor = White
            )
        },
        navigationIcon = {
            IconButton(onClick = navigateUp) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = stringResource(id = R.string.content_desc_back)
                )
            }
        },
    )
}