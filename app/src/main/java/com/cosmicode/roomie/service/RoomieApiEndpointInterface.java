package com.cosmicode.roomie.service;

import com.cosmicode.roomie.domain.Roomie;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface RoomieApiEndpointInterface {

    @GET("currentRoomie")
    Call<Roomie> getCurrentRoomie();

    @PUT("roomies")
    Call<Roomie> updateRoomie(@Body Roomie roomie);

    @POST("roomies")
    Call<Roomie> createRoomie(@Body Roomie roomie);

    @GET("roomies/{id}")
    Call<Roomie> findOneId(@Path("id") Long id);

    @GET("roomie-email/{email}")
    Call<Roomie> findOneByEmail(@Path("email") String email);
}
