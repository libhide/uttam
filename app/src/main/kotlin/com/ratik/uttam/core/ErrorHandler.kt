package com.ratik.uttam.core

import com.ratik.uttam.R
import javax.inject.Inject
import retrofit2.HttpException

interface ErrorHandler {

  fun getErrorMessage(error: Throwable): String
}

class ErrorHandlerImpl @Inject constructor(private val stringProvider: StringProvider) :
  ErrorHandler {

  override fun getErrorMessage(error: Throwable): String {
    return when (error) {
      is HttpException -> "Network failed"
      else -> stringProvider.getString(R.string.common_error)
    }
  }
}
