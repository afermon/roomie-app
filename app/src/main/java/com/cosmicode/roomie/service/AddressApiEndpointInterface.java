package com.cosmicode.roomie.service;

import com.cosmicode.roomie.domain.Address;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface AddressApiEndpointInterface {

    @GET("addresses/{id}")
    Call<Address> getAddressById(@Path("id") Long id);

}
