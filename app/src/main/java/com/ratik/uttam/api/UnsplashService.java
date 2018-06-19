package com.ratik.uttam.api;

import com.ratik.uttam.model.PhotoResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Ratik on 17/10/17.
 */

public interface UnsplashService {

    @GET("photos/random")
    Observable<PhotoResponse> getRandomPhoto(@Query("client_id") String clientId,
                                             @Query("collections") String collections,
                                             @Query("w") int w,
                                             @Query("h") int h);
}
