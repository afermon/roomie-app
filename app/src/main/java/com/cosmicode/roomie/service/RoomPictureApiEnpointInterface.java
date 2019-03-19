package com.cosmicode.roomie.service;

import com.cosmicode.roomie.domain.RoomPicture;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RoomPictureApiEnpointInterface {

    @POST("room-pictures")
    Call<Void> createPicture(@Body RoomPicture roomPicture);
}
