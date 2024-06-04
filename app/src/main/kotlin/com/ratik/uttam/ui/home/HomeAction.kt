package com.ratik.uttam.ui.home

internal sealed class HomeAction {
    object RefreshWallpaper : HomeAction()
    object SetWallpaper : HomeAction()
    object SaveWallpaper : HomeAction()
}