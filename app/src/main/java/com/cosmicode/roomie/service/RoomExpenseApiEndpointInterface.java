package com.cosmicode.roomie.service;

import com.cosmicode.roomie.domain.RoomExpense;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RoomExpenseApiEndpointInterface {

    @POST("room-expenses")
    Call<RoomExpense> createExpense(@Body RoomExpense roomExpense);


    @GET("expenses-room/{id}")
    Call<List<RoomExpense>>getExpenseByRoom(@Path("id") Long id);
}
