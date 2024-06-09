package com.ratik.uttam.core

import android.content.Context
import javax.inject.Inject

/**
 * StringProviderImpl helps providing strings if a string is needed below the view level such as in
 * ViewModels.
 */
class StringProviderImpl @Inject constructor(private val context: Context) : StringProvider {

  override fun getString(resId: Int): String {
    return context.getString(resId)
  }

  override fun getString(resId: Int, vararg args: Any): String {
    return context.getString(resId, *args)
  }
}
