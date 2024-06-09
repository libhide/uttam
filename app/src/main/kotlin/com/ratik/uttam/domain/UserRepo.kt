package com.ratik.uttam.domain

import com.ratik.uttam.data.dao.PrefsDao
import com.ratik.uttam.data.dao.UserDao
import javax.inject.Inject

internal class UserRepo
@Inject
constructor(private val userDao: UserDao, private val prefsDao: PrefsDao) {

  fun hasOnboarded(): Boolean {
    return userDao.hasOnboarded()
  }

  fun setHasOnboarded() {
    userDao.setHasOnboarded()
  }

  fun setDeviceHeight(height: Int) {
    userDao.setDeviceHeight(height)
  }

  fun setDeviceWidth(width: Int) {
    userDao.setDeviceWidth(width)
  }

  fun shouldSetWallpaperAutomatically(): Boolean {
    return prefsDao.shouldSetWallpaperAutomatically()
  }

  fun toggleSetWallpaperAutomatically() {
    prefsDao.toggleSetWallpaperAutomatically()
  }
}
