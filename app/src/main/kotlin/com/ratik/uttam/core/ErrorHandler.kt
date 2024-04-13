package com.ratik.uttam.core

import com.ratik.uttam.R
import retrofit2.HttpException
import javax.inject.Inject

interface ErrorHandler {

    fun getErrorMessage(error: Throwable): String
}

class ErrorHandlerImpl @Inject constructor(
    private val stringProvider: StringProvider,
) : ErrorHandler {

    override fun getErrorMessage(error: Throwable): String {
        return when (error) {
            is HttpException -> "Network failed"
            else -> stringProvider.getString(R.string.common_error)
        }
    }
}
