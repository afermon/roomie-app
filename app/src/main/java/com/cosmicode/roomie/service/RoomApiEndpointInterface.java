package com.cosmicode.roomie.service;

import com.cosmicode.roomie.domain.Room;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface RoomApiEndpointInterface {

    @GET("rooms")
    Call<List<Room>> getRooms();

}
