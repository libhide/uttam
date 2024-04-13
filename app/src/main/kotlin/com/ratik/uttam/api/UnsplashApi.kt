package com.ratik.uttam.api

import com.ratik.uttam.api.model.PhotoApiModel
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by Ratik on 17/10/17.
 */
interface UnsplashApi {
    @Deprecated("Use getRandomPhotoSus instead")
    @GET("photos/random")
    fun getRandomPhoto(
        @Query("client_id") clientId: String?,
        @Query("collections") collections: String?,
        @Query("w") w: Int,
        @Query("h") h: Int
    ): Observable<PhotoApiModel?>?

    @GET("photos/random")
    suspend fun getRandomPhotoSus(
        @Query("client_id") clientId: String?,
        @Query("collections") collections: String?,
        @Query("w") w: Int,
        @Query("h") h: Int
    ): Response<PhotoApiModel>
}