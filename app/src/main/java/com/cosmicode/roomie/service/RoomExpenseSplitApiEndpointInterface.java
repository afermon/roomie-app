package com.cosmicode.roomie.service;


import com.cosmicode.roomie.domain.RoomExpense;
import com.cosmicode.roomie.domain.RoomExpenseSplit;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RoomExpenseSplitApiEndpointInterface {

    @POST("room-expense-splits-list")
    Call<List<RoomExpenseSplit>> createExpenseSplit(@Body List<RoomExpenseSplit> roomExpenseSplit);


    @GET("split-expenses/{id}")
    Call<List<RoomExpenseSplit>>geSplittExpenseByExpense(@Path("id") Long id);
}
