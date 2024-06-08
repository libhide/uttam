package com.ratik.uttam.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Switch
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ratik.uttam.R
import com.ratik.uttam.ui.components.UttamText
import com.ratik.uttam.ui.components.VerticalSpacer
import com.ratik.uttam.ui.theme.ColorPrimary
import com.ratik.uttam.ui.theme.ColorPrimaryVariant
import com.ratik.uttam.ui.theme.Dimens.PERCENT_60
import com.ratik.uttam.ui.theme.Dimens.SpacingLarge
import com.ratik.uttam.ui.theme.Dimens.SpacingNormal
import com.ratik.uttam.ui.theme.Dimens.SpacingXLarge
import com.ratik.uttam.ui.theme.Dimens.SpacingXXXLarge
import com.ratik.uttam.ui.theme.Dimens.SpacingXXXXXXSmall
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

    val listState = rememberLazyListState()
    var setWallpaperAutomatically by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SettingsAppBar(
            modifier = Modifier
                .fillMaxWidth()
                .systemBarsPadding(),
            navigateUp = navigateUp
        )

        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(horizontal = SpacingNormal)
        ) {
            item {
                UttamText.CaptionBold(
                    text = stringResource(id = R.string.category_general),
                    textColor = ColorPrimaryVariant,
                )
            }

            item {
                VerticalSpacer(SpacingLarge)
            }

            item {
                SettingsRowItem(
                    title = stringResource(id = R.string.title_automatic_wallpaper_set),
                    description = stringResource(id = R.string.summary_automatic_wallpaper_set),
                    onItemClick = {
                        setWallpaperAutomatically = !setWallpaperAutomatically
                    },
                    action = {
                        Switch(
                            checked = setWallpaperAutomatically,
                            onCheckedChange = { _ ->
                                setWallpaperAutomatically = !setWallpaperAutomatically
                            },
                        )
                    }
                )
            }

            item {
                VerticalSpacer(SpacingXXXLarge)
            }

            item {
                UttamText.CaptionBold(
                    text = stringResource(id = R.string.category_misc),
                    textColor = ColorPrimaryVariant,
                )
            }

            item {
                VerticalSpacer(SpacingLarge)
            }

            item {
                SettingsRowItem(
                    title = stringResource(id = R.string.title_contact),
                    description = stringResource(id = R.string.summary_contact),
                    onItemClick = {},
                )
            }

            item {
                VerticalSpacer(SpacingXLarge)
            }

            item {
                SettingsRowItem(
                    title = stringResource(id = R.string.title_review),
                    description = stringResource(id = R.string.summary_review),
                    onItemClick = {},
                )
            }
        }
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

@Composable
private fun SettingsRowItem(
    title: String,
    description: String,
    onItemClick: () -> Unit,
    action: @Composable RowScope.() -> Unit = {},
) {
    Row(
        horizontalArrangement = SpaceBetween,
        verticalAlignment = CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onItemClick)
    ) {
        Column(
            modifier = Modifier.weight(if (action == {}) 1F else PERCENT_60)
        ) {
            UttamText.BodyBig(
                text = title,
                textColor = ColorPrimaryVariant
            )
            VerticalSpacer(SpacingXXXXXXSmall)
            UttamText.BodySmall(
                text = description,
                textColor = ColorPrimary
            )
        }
        action()
    }
}