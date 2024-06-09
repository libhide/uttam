package com.ratik.uttam.data.extensions

import com.ratik.uttam.data.ApiResult
import com.ratik.uttam.data.exceptions.GeneralApiException
import com.ratik.uttam.data.exceptions.UnauthorizedException
import java.io.IOException
import java.net.HttpURLConnection.HTTP_UNAUTHORIZED
import retrofit2.HttpException
import retrofit2.Response

fun <T : Any> Response<T>.asResult(): ApiResult<T> {
  return try {
    return apiResult(this)
  } catch (e: Exception) {
    when (e) {
      is IOException -> ApiResult.Failure(e) // Network error
      is HttpException -> { // Unexpected non-2xx error
        e.response()?.let { ApiResult.Failure(e, it.errorBody()?.string()) } ?: ApiResult.Failure(e)
      }
      else -> ApiResult.Failure(e) // Unknown Error
    }
  }
}

private fun <T : Any> apiResult(response: Response<T>): ApiResult<T> {
  return when {
    response.isSuccessful && response.body() != null -> ApiResult.Success(response.body()!!)
    else -> {
      val errorMessage = parseErrorMessage(response)
      when (response.code()) {
        HTTP_UNAUTHORIZED -> ApiResult.Failure(UnauthorizedException(), errorMessage)
      }
      val errorResponse = response.errorBody()?.string()
      ApiResult.Failure(GeneralApiException(), errorResponse)
    }
  }
}

private fun <T> parseErrorMessage(response: Response<T>): String {
  return response.errorBody()?.string() ?: "Some error message"
}
