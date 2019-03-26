package com.cosmicode.roomie.service;

import com.cosmicode.roomie.domain.RoomExpense;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RoomExpenseApiEndpointInterface {

    @POST("room-expenses")
    Call<RoomExpense> createExpense(@Body RoomExpense roomExpense);
}
