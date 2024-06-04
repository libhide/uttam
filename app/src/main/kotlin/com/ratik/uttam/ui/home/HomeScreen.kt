package com.ratik.uttam.ui.home

import android.app.Activity.RESULT_OK
import android.app.WallpaperManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import coil.request.ImageRequest
import com.fueled.android.core.ui.extensions.collectAsEffect
import com.fueled.android.core.ui.extensions.rememberFlowOnLifecycle
import com.ratik.uttam.R
import com.ratik.uttam.core.Ignored
import com.ratik.uttam.core.MessageState.Snack
import com.ratik.uttam.core.contract.ViewEvent.DisplayMessage
import com.ratik.uttam.core.contract.ViewEvent.Effect
import com.ratik.uttam.ui.components.HorizontalSpacer
import com.ratik.uttam.ui.components.ScrollableImage
import com.ratik.uttam.ui.home.HomeAction.RefreshWallpaper
import com.ratik.uttam.ui.home.HomeAction.SetWallpaper
import com.ratik.uttam.ui.home.HomeEffect.ChangeWallpaper
import com.ratik.uttam.ui.modifiers.shimmerBackground
import com.ratik.uttam.ui.theme.Dimens
import com.ratik.uttam.ui.theme.Dimens.SpacingLarge
import com.ratik.uttam.ui.theme.Dimens.SpacingMedium
import com.ratik.uttam.ui.theme.Dimens.SpacingNormal
import com.ratik.uttam.ui.theme.Dimens.SpacingXLarge
import com.ratik.uttam.ui.theme.Dimens.SpacingXSmall
import com.ratik.uttam.ui.theme.Dimens.TextSizeDefault
import com.ratik.uttam.ui.theme.Dimens.TextSizeLarge
import com.ratik.uttam.ui.theme.Dimens.TextSizeSmall
import com.ratik.uttam.ui.theme.Dimens.TextSizeXXLarge
import timber.log.Timber
import java.io.File

@Composable
internal fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val wallpaperManager = WallpaperManager.getInstance(context)
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if (activityResult.resultCode == RESULT_OK) {
                // No-op
            }
        }

    val state by rememberFlowOnLifecycle(flow = viewModel.state).collectAsState(HomeState.initialState)

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
                        val wallpaperUri = FileProvider.getUriForFile(
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
        Row {
            Image(
                painter = painterResource(id = R.drawable.uttam),
                contentDescription = stringResource(id = R.string.content_desc_app_logo),
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

            Row {
                Image(
                    painter = painterResource(id = R.drawable.ic_download),
                    contentDescription = stringResource(id = R.string.content_desc_refresh_wallpaper),
                    modifier = Modifier.clickable {
                        viewModel.onViewAction(RefreshWallpaper)
                    },
                )
                HorizontalSpacer(size = SpacingXSmall)
                Image(
                    painter = painterResource(id = R.drawable.ic_set),
                    contentDescription = stringResource(id = R.string.content_desc_set_wallpaper),
                    modifier = Modifier.clickable {
                        viewModel.onViewAction(SetWallpaper)
                    },
                )
            }
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