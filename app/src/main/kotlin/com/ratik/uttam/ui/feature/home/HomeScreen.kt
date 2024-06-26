package com.ratik.uttam.ui.feature.home

import android.app.WallpaperManager
import android.content.Intent
import android.content.Intent.createChooser
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.content.FileProvider.getUriForFile
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import coil.request.ImageRequest
import com.ratik.uttam.R
import com.ratik.uttam.core.Ignored
import com.ratik.uttam.core.MessageState.Snack
import com.ratik.uttam.core.contract.ViewEvent.DisplayMessage
import com.ratik.uttam.core.contract.ViewEvent.Effect
import com.ratik.uttam.ui.components.ScrollableImage
import com.ratik.uttam.ui.components.UttamText
import com.ratik.uttam.ui.extensions.collectAsEffect
import com.ratik.uttam.ui.extensions.rememberFlowOnLifecycle
import com.ratik.uttam.ui.feature.home.HomeAction.RefreshWallpaper
import com.ratik.uttam.ui.feature.home.HomeAction.SetWallpaper
import com.ratik.uttam.ui.feature.home.HomeEffect.LaunchCropAndSetWallpaperFlow
import com.ratik.uttam.ui.feature.home.HomeEffect.SetWallpaperSilently
import com.ratik.uttam.ui.modifiers.shimmerBackground
import com.ratik.uttam.ui.theme.ColorPrimary
import com.ratik.uttam.ui.theme.ColorPrimaryVariant
import com.ratik.uttam.ui.theme.Dimens.NoElevation
import com.ratik.uttam.ui.theme.Dimens.PERCENT_10
import com.ratik.uttam.ui.theme.Dimens.SpacingNormal
import com.ratik.uttam.ui.theme.setNavigationBarColors
import com.ratik.uttam.ui.theme.setStatusBarColors
import java.io.File

@Composable
internal fun HomeScreen(
  viewModel: HomeViewModel = hiltViewModel(),
  navigateToSettings: () -> Unit,
) {
  setStatusBarColors(isDarkIcons = false, color = Transparent)
  setNavigationBarColors(isDarkIcons = false, backgroundColor = Transparent)

  val context = LocalContext.current
  val wallpaperManager = WallpaperManager.getInstance(context)
  val launcher =
    rememberLauncherForActivityResult(StartActivityForResult()) {}

  val state by rememberFlowOnLifecycle(flow = viewModel.state)
    .collectAsState(HomeState.initialState)

  viewModel.events.collectAsEffect { event ->
    when (event) {
      is DisplayMessage -> {
        when (event.message) {
          is Snack -> {
            Toast.makeText(context, event.message.message, Toast.LENGTH_SHORT).show()
          }

          else -> Ignored
        }
      }

      is Effect -> {
        when (event.effect) {
          is LaunchCropAndSetWallpaperFlow -> {
            val wallpaperFile = File(state.currentWallpaper!!.rawPhotoUri)
            val wallpaperUri =
              getUriForFile(context, "${context.packageName}.provider", wallpaperFile)
            val wallpaperSetIntent = wallpaperManager.getCropAndSetWallpaperIntent(wallpaperUri)
            launcher.launch(wallpaperSetIntent)
          }

          is SetWallpaperSilently -> {
            val wallpaperFile = File(state.currentWallpaper!!.rawPhotoUri)
            val wallpaperUri =
              getUriForFile(context, "${context.packageName}.provider", wallpaperFile)
            wallpaperManager.setStream(context.contentResolver.openInputStream(wallpaperUri))
            Toast.makeText(context, R.string.wallpaper_set_text, Toast.LENGTH_SHORT).show()
          }
        }
      }

      else -> Ignored
    }
  }

  Box(
    modifier =
    Modifier
      .fillMaxSize()
      .background(ColorPrimary), // TODO: Use palette API to generate this at refresh time
  ) {
    ScrollableImage(
      model =
      ImageRequest.Builder(context)
        .data(state.currentWallpaper?.rawPhotoUri)
        .crossfade(true)
        .build(),
      contentDescription = null,
    )

    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Color.Black.copy(alpha = PERCENT_10)),
    )

    HomeAppBar(
      modifier = Modifier
        .fillMaxWidth()
        .align(TopCenter)
        .systemBarsPadding(),
      navigateToSettings = navigateToSettings,
      refreshWallpaper = { viewModel.onViewAction(RefreshWallpaper) },
      shareWallpaper = {
        val shareIntent = Intent(Intent.ACTION_SEND)
        val shareText =
          String.format(
            context.getString(R.string.wallpaper_share_text),
            state.currentWallpaper?.photographer?.name.orEmpty(),
            state.currentWallpaper?.shareUrl.orEmpty(),
          )
        shareIntent.setType("text/plain")
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText)
        context.startActivity(
          createChooser(shareIntent, context.getString(R.string.share_label)),
        )
      },
    )

    Row(
      horizontalArrangement = SpaceBetween,
      verticalAlignment = CenterVertically,
      modifier = Modifier
        .fillMaxWidth()
        .align(BottomCenter)
        .navigationBarsPadding()
        .padding(horizontal = SpacingNormal),
    ) {
      Column(
        modifier = Modifier.clickable {
          val photographerUrl = state.currentWallpaper?.photographer?.profileUrl.orEmpty()
          if (photographerUrl.isNotEmpty()) {
            context.startActivity(Intent(Intent.ACTION_VIEW, photographerUrl.toUri()))
          }
        },
      ) {
        UttamText.BodySmall(
          text = stringResource(id = R.string.photographer_label),
          textColor = White,
        )
        UttamText.Body(
          text = state.currentWallpaper?.photographer?.name.orEmpty(),
          textColor = White,
        )
      }

      IconButton(onClick = { viewModel.onViewAction(SetWallpaper) }) {
        Icon(
          painter = painterResource(id = R.drawable.ic_set),
          contentDescription = stringResource(id = R.string.content_desc_set_wallpaper),
        )
      }
    }

    if (state.isLoading) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .shimmerBackground(),
      )
    }
  }
}

@Composable
private fun HomeAppBar(
  modifier: Modifier = Modifier,
  navigateToSettings: () -> Unit,
  refreshWallpaper: () -> Unit,
  shareWallpaper: () -> Unit,
) {
  var showMenu by remember { mutableStateOf(false) }

  TopAppBar(
    modifier = modifier,
    backgroundColor = Transparent,
    elevation = NoElevation,
    title = {
      Image(
        painter = painterResource(id = R.drawable.uttam),
        contentDescription = stringResource(id = R.string.content_desc_app_logo),
      )
    },
    actions = {
      IconButton(onClick = refreshWallpaper) {
        Icon(
          painter = painterResource(id = R.drawable.ic_refresh),
          contentDescription = stringResource(id = R.string.content_desc_refresh_wallpaper),
        )
      }
      IconButton(onClick = { showMenu = true }) {
        Icon(
          painter = painterResource(id = R.drawable.ic_overflow),
          contentDescription = stringResource(id = R.string.content_desc_overflow_menu),
        )
      }
      if (showMenu) {
        DropdownMenu(expanded = true, onDismissRequest = { showMenu = false }) {
          DropdownMenuItem(onClick = shareWallpaper) {
            Text(
              text = stringResource(id = R.string.share_label),
              color = ColorPrimaryVariant,
            )
          }
          DropdownMenuItem(onClick = navigateToSettings) {
            Text(
              text = stringResource(id = R.string.settings_label),
              color = ColorPrimaryVariant,
            )
          }
        }
      }
    },
  )
}
