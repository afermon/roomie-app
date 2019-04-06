package com.cosmicode.roomie.service;

import com.cosmicode.roomie.domain.RoomFeature;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RoomFeatureApiEndpointInterface {

    @GET("room-features?size=50")
    Call<List<RoomFeature>> getAllFeatures();
}
