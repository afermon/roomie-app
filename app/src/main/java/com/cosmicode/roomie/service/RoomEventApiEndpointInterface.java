package com.cosmicode.roomie.service;

import com.cosmicode.roomie.domain.RoomEvent;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface RoomEventApiEndpointInterface {
    @GET("room-events/room/{id}")
    Call<List<RoomEvent>> getRoomRoomEvents(@Path("id") Long id);

    @GET("room-events/{id}")
    Call<RoomEvent> getRoomEvent(@Path("id") Long id);

    @POST("room-events")
    Call<RoomEvent> createRoomEvent(@Body RoomEvent roomEvent);

    @PUT("room-events")
    Call<RoomEvent> updateRoomEvent(@Body RoomEvent roomEvent);

    @DELETE("room-events/{id}")
    Call<Void> deleteRoomEvent(@Path("id") Long id);
}
