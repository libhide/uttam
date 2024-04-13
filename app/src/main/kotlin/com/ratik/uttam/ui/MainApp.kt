package com.ratik.uttam.ui

import android.app.Activity.RESULT_OK
import android.app.WallpaperManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.ratik.uttam.R
import com.ratik.uttam.ui.components.ScrollableImage
import com.ratik.uttam.ui.theme.Dimens.SpacingNormal
import com.ratik.uttam.ui.theme.Dimens.TextSizeXXLarge
import com.ratik.uttam.ui.theme.UttamTheme
import java.io.File

@Composable
internal fun MainApp() {
    val context = LocalContext.current
    val wallpaper = painterResource(id = R.drawable.uttam_hero)
    val wallpaperManager = WallpaperManager.getInstance(context)
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if (activityResult.resultCode == RESULT_OK) {
                Toast.makeText(context, R.string.wallpaper_set_text, Toast.LENGTH_SHORT).show()
            }
        }

    UttamTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            ScrollableImage(
                painter = wallpaper,
                contentDescription = null,
            )
            Column(
                modifier = Modifier
                    .safeDrawingPadding()
                    .padding(horizontal = SpacingNormal),
            ) {
                Button(
                    onClick = {
                        // Setting with intent
                        val drawable = ContextCompat.getDrawable(context, R.drawable.uttam_hero)

                        val bitmap = (drawable?.mutate() as BitmapDrawable).bitmap.copy(
                            Bitmap.Config.ARGB_8888,
                            true
                        )

                        val canvas = Canvas(bitmap)

                        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)

                        drawable.draw(canvas)

                        val tempFile = File.createTempFile(
                            "wallpaper",
                            ".jpg",
                            context.cacheDir
                        )

                        val outputStream = tempFile.outputStream()

                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

                        outputStream.close()

                        val uri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.provider",
                            tempFile
                        )

                        val wallpaperSetIntent = wallpaperManager.getCropAndSetWallpaperIntent(uri)

                        launcher.launch(wallpaperSetIntent)

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
                        text = "Uttam",
                        color = Color.White,
                        fontSize = TextSizeXXLarge,
                    )
                }
            }
        }
    }
}
