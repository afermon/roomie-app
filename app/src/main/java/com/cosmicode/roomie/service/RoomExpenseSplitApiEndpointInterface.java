package com.cosmicode.roomie.service;


import com.cosmicode.roomie.domain.RoomExpenseSplit;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RoomExpenseSplitApiEndpointInterface {

    @POST("room-expense-splits-list")
    Call<List<RoomExpenseSplit>> createExpenseSplit(@Body List<RoomExpenseSplit> roomExpenseSplit);
}
