package com.cosmicode.roomie.service;

import com.cosmicode.roomie.domain.UserReport;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserReportApiEndpointInterface {

    @GET("user-report/{id}")
    Call<UserReport> getUserReportById(@Path("id") Long id);

    @PUT("user-report")
    Call<UserReport> updateUserReport(@Body UserReport userReport);

    @POST("user-report")
    Call<UserReport> createUserReport(@Body UserReport userReport);

}
