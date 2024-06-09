package com.ratik.uttam.ui.feature.settings

internal sealed class SettingsAction {
  data object ToggleSetWallpaperAutomatically : SettingsAction()
}
