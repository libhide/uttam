package com.ratik.uttam.domain

internal interface WallpaperSetter {
  suspend fun setHomeScreen(wallpaperPath: String): Result<Unit>
}
