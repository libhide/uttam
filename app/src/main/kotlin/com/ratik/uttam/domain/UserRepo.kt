package com.ratik.uttam.domain

import com.ratik.uttam.data.dao.UserDao
import javax.inject.Inject

internal class UserRepo @Inject constructor(
    private val userDao: UserDao,
) {

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
}