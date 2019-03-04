package com.cosmicode.roomie.service;

import com.cosmicode.roomie.domain.Room;
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
}
