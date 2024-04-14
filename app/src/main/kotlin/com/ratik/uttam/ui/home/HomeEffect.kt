package com.ratik.uttam.ui.home

import com.fueled.android.core.common.contract.SideEffect

internal sealed class HomeEffect: SideEffect {
    object ChangeWallpaper : HomeEffect()
}