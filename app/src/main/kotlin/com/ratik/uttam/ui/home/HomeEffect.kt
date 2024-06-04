package com.ratik.uttam.ui.home

import com.fueled.android.core.common.contract.SideEffect
import java.io.File

internal sealed class HomeEffect: SideEffect {
    object ChangeWallpaper : HomeEffect()
    data class NotifyWallpaperSaved(val savedWallpaperFilePath: String): HomeEffect()
}