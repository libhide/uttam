package com.ratik.uttam.ui.feature.home

internal sealed class HomeAction {
  data object RefreshWallpaper : HomeAction()

  data object SetWallpaper : HomeAction()
}
