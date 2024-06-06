package com.ratik.uttam.ui.home

import android.app.Activity.RESULT_OK
import android.app.WallpaperManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.content.FileProvider.getUriForFile
import androidx.hilt.navigation.compose.hiltViewModel
import coil.request.ImageRequest
import com.fueled.android.core.ui.extensions.collectAsEffect
import com.fueled.android.core.ui.extensions.rememberFlowOnLifecycle
import com.ratik.uttam.R
import com.ratik.uttam.core.Ignored
import com.ratik.uttam.core.MessageState.Snack
import com.ratik.uttam.core.contract.ViewEvent.DisplayMessage
import com.ratik.uttam.core.contract.ViewEvent.Effect
import com.ratik.uttam.ui.components.ScrollableImage
import com.ratik.uttam.ui.home.HomeAction.RefreshWallpaper
import com.ratik.uttam.ui.home.HomeAction.SetWallpaper
import com.ratik.uttam.ui.home.HomeEffect.ChangeWallpaper
import com.ratik.uttam.ui.modifiers.shimmerBackground
import com.ratik.uttam.ui.theme.Dimens.SpacingLarge
import com.ratik.uttam.ui.theme.Dimens.SpacingMedium
import com.ratik.uttam.ui.theme.Dimens.TextSizeDefault
import com.ratik.uttam.ui.theme.Dimens.TextSizeSmall
import java.io.File

@Composable
internal fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val wallpaperManager = WallpaperManager.getInstance(context)
    val launcher =
        rememberLauncherForActivityResult(StartActivityForResult()) { activityResult ->
            if (activityResult.resultCode == RESULT_OK) {
                // No-op
                // The system shows a toast message when the wallpaper is set
            }
        }

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
                    is ChangeWallpaper -> {
                        val wallpaperFile = File(state.currentWallpaper!!.localUri)
                        val wallpaperUri = getUriForFile(
                            context, "${context.packageName}.provider", wallpaperFile
                        )
                        val wallpaperSetIntent =
                            wallpaperManager.getCropAndSetWallpaperIntent(wallpaperUri)
                        launcher.launch(wallpaperSetIntent)
                    }
                }
            }

            else -> Ignored
        }
    }

    ScrollableImage(
        model = ImageRequest.Builder(context)
            .data(state.currentWallpaper?.localUri ?: R.drawable.uttam_hero).crossfade(true)
            .build(),
        contentDescription = null,
    )

    Column(
        verticalArrangement = SpaceBetween,
        modifier = Modifier
            .systemBarsPadding()
            .padding(
                horizontal = SpacingMedium,
                vertical = SpacingLarge,
            ),
    ) {
        Row(
            horizontalArrangement = SpaceBetween,
            verticalAlignment = CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Image(
                painter = painterResource(id = R.drawable.uttam),
                contentDescription = stringResource(id = R.string.content_desc_app_logo),
            )
            Image(
                painter = painterResource(id = R.drawable.ic_refresh),
                contentDescription = stringResource(id = R.string.content_desc_refresh_wallpaper),
                modifier = Modifier.clickable {
                    viewModel.onViewAction(RefreshWallpaper)
                },
            )
        }

        Row(
            horizontalArrangement = SpaceBetween,
            verticalAlignment = CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column {
                Text(
                    text = stringResource(id = R.string.photographer_label),
                    color = White,
                    fontSize = TextSizeSmall,
                )
                Text(
                    text = state.currentWallpaper?.photographer?.name.orEmpty(),
                    color = White,
                    fontSize = TextSizeDefault,
                )
            }

            Image(
                painter = painterResource(id = R.drawable.ic_set),
                contentDescription = stringResource(id = R.string.content_desc_set_wallpaper),
                modifier = Modifier.clickable {
                    viewModel.onViewAction(SetWallpaper)
                },
            )
        }
    }

    if (state.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shimmerBackground()
        )
    }
}