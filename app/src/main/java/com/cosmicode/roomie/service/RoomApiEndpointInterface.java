package com.cosmicode.roomie.service;

import com.cosmicode.roomie.domain.Room;
import com.cosmicode.roomie.domain.RoomCreate;
import com.cosmicode.roomie.domain.SearchFilter;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RoomApiEndpointInterface {

    @GET("rooms")
    Call<List<Room>> getRooms();

    @GET("_search/rooms")
    Call<List<Room>> serachRooms(@Query("query") String string);

    @POST("rooms")
    Call<Room> createRoom(@Body RoomCreate room);

    @PUT("rooms")
    Call<Room> updateRoom(@Body RoomCreate room);

    @PUT("rooms")
    Call<Room> updateRoomInfo(@Body Room room);

    @POST("_search/rooms/advanced")
    Call<List<Room>> searchRoomsAdvanced(@Body SearchFilter searchFilter);

    @GET("owned-rooms/{id}")
    Call<List<Room>> getOwnedRooms(@Path("id") Long id);

    @POST("pay-room/{token}")
    Call<Room> payPremium(@Body Room room, @Path("token") String token);

    @GET("owned-premium-rooms/{id}")
    Call<List<Room>> getOwnedPremiumRooms(@Path("id") Long id);

    @GET("price")
    Call<Double> getPrice();
}
