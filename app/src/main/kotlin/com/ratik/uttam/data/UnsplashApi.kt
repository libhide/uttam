package com.ratik.uttam.data

import com.ratik.uttam.data.model.PhotoApiModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

/** Created by Ratik on 17/10/17. */
interface UnsplashApi {
  @GET("photos/random")
  suspend fun getRandomPhotoSus(
    @Query("client_id") clientId: String?,
    @Query("collections") collections: String?,
    @Query("w") w: Int? = null,
    @Query("h") h: Int? = null,
  ): Response<PhotoApiModel>

  @GET("photos/{id}")
  suspend fun getPhoto(
    @Path("id") photoId: String,
    @Query("client_id") clientId: String?,
    @Query("w") w: Int? = null,
    @Query("h") h: Int? = null,
  ): Response<PhotoApiModel>

  @GET
  suspend fun incrementDownloadCount(
    @Url url: String,
    @Query("client_id") clientId: String?,
  ): Response<Unit>
}
