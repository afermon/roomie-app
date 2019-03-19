package com.cosmicode.roomie.service;

import com.cosmicode.roomie.domain.RoomFeature;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RoomFeatureApiEndpointInterface {

    @GET("room-features")
    Call<List<RoomFeature>> getAllFeatures();
}
