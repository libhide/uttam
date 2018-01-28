package com.ratik.uttam.api;

import com.ratik.uttam.model._Photo;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Ratik on 17/10/17.
 */

public interface UnsplashService {

    @GET("photos/random")
    Call<_Photo> getRandomPhoto(@Query("client_id") String clientId,
                                @Query("collections") String collections);

    @GET("photos/random")
    Observable<Response<_Photo>> getRandomPhoto2(@Query("client_id") String clientId,
                                                 @Query("collections") String collections);
}
