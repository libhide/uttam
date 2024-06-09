package com.ratik.uttam.ui.feature.settings

import android.content.Intent
import android.net.Uri
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ratik.uttam.Constants.EMAIL
import com.ratik.uttam.R
import com.ratik.uttam.ui.components.UttamText
import com.ratik.uttam.ui.components.VerticalSpacer
import com.ratik.uttam.ui.extensions.rememberFlowOnLifecycle
import com.ratik.uttam.ui.feature.settings.SettingsAction.ToggleSetWallpaperAutomatically
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
internal fun SettingsScreen(
  viewModel: SettingsViewModel = hiltViewModel(),
  navigateUp: () -> Unit,
) {
  setStatusBarColors(isDarkIcons = false, color = ColorPrimary)
  setNavigationBarColors(isDarkIcons = false, backgroundColor = ColorPrimaryVariant)

  val context = LocalContext.current

  val listState = rememberLazyListState()

  val state by
    rememberFlowOnLifecycle(flow = viewModel.state).collectAsState(SettingsState.initialState)

  Column(modifier = Modifier.fillMaxSize()) {
    SettingsAppBar(modifier = Modifier.fillMaxWidth().systemBarsPadding(), navigateUp = navigateUp)

    LazyColumn(state = listState, contentPadding = PaddingValues(horizontal = SpacingNormal)) {
      item {
        UttamText.CaptionBold(
          text = stringResource(id = R.string.category_general),
          textColor = ColorPrimaryVariant,
        )
      }

      item { VerticalSpacer(SpacingLarge) }

      item {
        SettingsRowItem(
          title = stringResource(id = R.string.title_automatic_wallpaper_set),
          description = stringResource(id = R.string.summary_automatic_wallpaper_set),
          onItemClick = { viewModel.onViewAction(ToggleSetWallpaperAutomatically) },
          action = {
            Switch(
              checked = state.setWallpaperAutomatically,
              onCheckedChange = { _ ->
                viewModel.onViewAction(ToggleSetWallpaperAutomatically)
              },
            )
          },
        )
      }

      item { VerticalSpacer(SpacingXXXLarge) }

      item {
        UttamText.CaptionBold(
          text = stringResource(id = R.string.category_misc),
          textColor = ColorPrimaryVariant,
        )
      }

      item { VerticalSpacer(SpacingLarge) }

      item {
        SettingsRowItem(
          title = stringResource(id = R.string.title_contact),
          description = stringResource(id = R.string.summary_contact),
          onItemClick = {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "message/rfc822"
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(EMAIL))
            context.startActivity(intent)
          },
        )
      }

      item { VerticalSpacer(SpacingXLarge) }

      item {
        SettingsRowItem(
          title = stringResource(id = R.string.title_review),
          description = stringResource(id = R.string.summary_review),
          onItemClick = {
            val intent =
              Intent(
                Intent.ACTION_VIEW,
                Uri.parse("market://details?id=${context.packageName}"),
              )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
          },
        )
      }
    }
  }
}

@Composable
private fun SettingsAppBar(modifier: Modifier = Modifier, navigateUp: () -> Unit) {
  TopAppBar(
    modifier = modifier,
    elevation = 0.dp,
    title = {
      UttamText.AppBar(text = stringResource(id = R.string.settings_label), textColor = White)
    },
    navigationIcon = {
      IconButton(onClick = navigateUp) {
        Icon(
          painter = painterResource(id = R.drawable.ic_back),
          contentDescription = stringResource(id = R.string.content_desc_back),
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
    modifier = Modifier.fillMaxWidth().clickable(onClick = onItemClick),
  ) {
    Column(modifier = Modifier.weight(if (action == {}) 1F else PERCENT_60)) {
      UttamText.BodyBig(text = title, textColor = ColorPrimaryVariant)
      VerticalSpacer(SpacingXXXXXXSmall)
      UttamText.BodySmall(text = description, textColor = ColorPrimary)
    }
    action()
  }
}
