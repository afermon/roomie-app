package com.cosmicode.roomie.service;

import com.cosmicode.roomie.domain.Address;
import com.cosmicode.roomie.domain.Room;
import com.cosmicode.roomie.domain.RoomTask;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface RoomTasksApiEndpointInterface {

    @GET("taskByRoom/{id}")
    Call<List<RoomTask>>getTaskByRoom(@Path("id") Long id);
}
