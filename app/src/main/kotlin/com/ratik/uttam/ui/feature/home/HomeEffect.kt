package com.ratik.uttam.ui.feature.home

import com.fueled.android.core.common.contract.SideEffect

internal sealed class HomeEffect : SideEffect {
  data object ChangeWallpaper : HomeEffect()
}
