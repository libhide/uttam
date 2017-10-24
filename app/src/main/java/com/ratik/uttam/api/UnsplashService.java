package com.ratik.uttam.api;

import com.ratik.uttam.model._Photo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Ratik on 17/10/17.
 */

public interface UnsplashService {

    @GET("photos/random")
    Call<_Photo> getRandomPhoto(@Query("client_id") String clientId,
                                @Query("category") String category);
}
