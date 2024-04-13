package com.ratik.uttam.data

/**
 * A sealed wrapper class to wrap the results of all API calls
 */
sealed class ApiResult<out T> {
    class Success<out T>(val data: T) : ApiResult<T>()
    class Failure(val throwable: Throwable, val message: String? = null) : ApiResult<Nothing>()
}

inline fun <T> ApiResult<T>.whenSuccess(block: (result: T) -> Unit): ApiResult<T> {
    if (this is ApiResult.Success && data != null) {
        block(this.data)
    }
    return this
}

inline fun <T> ApiResult<T>.whenError(block: (error: Throwable) -> Unit): ApiResult<T> {
    if (this is ApiResult.Failure) {
        block(this.throwable)
    }
    return this
}

inline fun <T, R> ApiResult<T>.map(mapper: (T) -> R): ApiResult<R> =
    try {
        ApiResult.Success(mapper((this as ApiResult.Success).data))
    } catch (e: Throwable) {
        ApiResult.Failure(e)
    }
