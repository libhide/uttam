package com.ratik.uttam.api;

import com.ratik.uttam.model.PhotoResponse;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Ratik on 17/10/17.
 */

public interface UnsplashService {

    @GET("photos/random")
    Observable<Response<PhotoResponse>> getRandomPhoto(@Query("client_id") String clientId,
                                                       @Query("collections") String collections);
}
