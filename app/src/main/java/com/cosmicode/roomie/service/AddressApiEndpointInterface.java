package com.cosmicode.roomie.service;

import com.cosmicode.roomie.domain.Address;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface AddressApiEndpointInterface {

    @GET("addresses/{id}")
    Call<Address> getAddressById(@Path("id") Long id);

    @PUT("addresses")
    Call<Address> updateAddress(@Body Address address);

    @POST("addresses")
    Call<Address> createAddress(@Body Address address);

}
