package com.ratik.uttam.ui.home

import android.app.Activity.RESULT_OK
import android.app.WallpaperManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.fueled.android.core.ui.extensions.collectAsEffect
import com.fueled.android.core.ui.extensions.rememberFlowOnLifecycle
import com.ratik.uttam.R
import com.ratik.uttam.core.Ignored
import com.ratik.uttam.core.MessageState
import com.ratik.uttam.core.MessageState.Snack
import com.ratik.uttam.core.contract.ViewEvent
import com.ratik.uttam.core.contract.ViewEvent.DisplayMessage
import com.ratik.uttam.ui.components.ScrollableImage
import com.ratik.uttam.ui.home.HomeAction.RefreshWallpaper
import com.ratik.uttam.ui.theme.Dimens.SpacingNormal
import com.ratik.uttam.ui.theme.Dimens.TextSizeXXLarge
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
                Toast.makeText(context, R.string.wallpaper_set_text, Toast.LENGTH_SHORT).show()
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

            else -> Ignored
        }
    }

    ScrollableImage(
        model = ImageRequest.Builder(context)
            .data(state.currentWallpaper?.localUri ?: R.drawable.uttam_hero)
            .crossfade(true)
            .build(),
        contentDescription = null,
    )

    if (state.isLoading) {
        CircularProgressIndicator(color = Color.White)
    }

    Column(
        modifier = Modifier
            .safeDrawingPadding()
            .padding(horizontal = SpacingNormal),
    ) {
        Button(
            onClick = {
                viewModel.onViewAction(RefreshWallpaper)
                // Setting with intent
//                val drawable = ContextCompat.getDrawable(context, R.drawable.uttam_hero)
//
//                val bitmap = (drawable?.mutate() as BitmapDrawable).bitmap.copy(
//                    Bitmap.Config.ARGB_8888,
//                    true
//                )
//
//                val canvas = Canvas(bitmap)
//
//                drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
//
//                drawable.draw(canvas)
//
//                val tempFile = File.createTempFile(
//                    "wallpaper",
//                    ".jpg",
//                    context.cacheDir
//                )
//
//                val outputStream = tempFile.outputStream()
//
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
//
//                outputStream.close()
//
//                val uri = FileProvider.getUriForFile(
//                    context,
//                    "${context.packageName}.provider",
//                    tempFile
//                )
//
//                val wallpaperSetIntent = wallpaperManager.getCropAndSetWallpaperIntent(uri)
//
//                launcher.launch(wallpaperSetIntent)

                // Setting directly
//                        val drawable = ContextCompat.getDrawable(context, R.drawable.uttam_hero)
//
//                        val bitmap = (drawable?.mutate() as BitmapDrawable).bitmap.copy(
//                            Bitmap.Config.ARGB_8888,
//                            true
//                        )
//
//                        wallpaperManager.setBitmap(bitmap)
            },
        ) {
            Text(
                text = "Refresh Wallpaper",
                color = Color.White,
                fontSize = TextSizeXXLarge,
            )
        }
    }
}