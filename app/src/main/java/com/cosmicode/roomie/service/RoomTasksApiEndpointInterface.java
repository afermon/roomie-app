package com.cosmicode.roomie.service;

import com.cosmicode.roomie.domain.Address;
import com.cosmicode.roomie.domain.Room;
import com.cosmicode.roomie.domain.RoomTask;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface RoomTasksApiEndpointInterface {

    @GET("tasksByRoom/{id}")
    Call<List<RoomTask>>getTaskByRoom(@Path("id") Long id);

    @POST("room-tasks")
    Call<RoomTask>createTask(@Body RoomTask roomTask);

    @DELETE("room-tasks/{id}")
    Call<Void>deleteTask(@Path("id") Long id);

    @PUT("room-tasks")
    Call<RoomTask>editTask(@Body RoomTask roomtask);
}
