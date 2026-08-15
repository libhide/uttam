package com.ratik.uttam.data.storage

import android.app.WallpaperManager
import android.app.WallpaperManager.FLAG_SYSTEM
import android.content.Context
import com.ratik.uttam.core.DispatcherProvider
import com.ratik.uttam.domain.WallpaperSetter
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

internal class AndroidWallpaperSetter @Inject constructor(
  context: Context,
  private val dispatcherProvider: DispatcherProvider,
) : WallpaperSetter {
  private val wallpaperManager = WallpaperManager.getInstance(context)

  override suspend fun setHomeScreen(wallpaperPath: String): Result<Unit> =
    withContext(dispatcherProvider.io) {
      runCatching {
        check(wallpaperManager.isWallpaperSupported) {
          "Wallpapers are not supported for this user"
        }
        check(wallpaperManager.isSetWallpaperAllowed) {
          "Setting wallpapers is disabled for this user"
        }

        val wallpaperFile = File(wallpaperPath)
        check(wallpaperFile.isFile && wallpaperFile.length() > 0) {
          "Wallpaper file is unavailable"
        }

        wallpaperFile.inputStream().buffered().use { inputStream ->
          wallpaperManager.setStream(inputStream, null, true, FLAG_SYSTEM)
        }
        Unit
      }
    }
}
