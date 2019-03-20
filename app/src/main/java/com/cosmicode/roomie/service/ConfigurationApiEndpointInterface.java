package com.cosmicode.roomie.service;

import com.cosmicode.roomie.domain.Address;
import com.cosmicode.roomie.domain.UserPreferences;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ConfigurationApiEndpointInterface {

    @GET("user-preferences/{id}")
    Call<UserPreferences> getUserPreferenceById(@Path("id") Long id);

    @PUT("user-preferences")
    Call<UserPreferences> updateUserPreference(@Body UserPreferences userPreferences);




}
