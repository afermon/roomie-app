package com.cosmicode.roomie.service;

import com.cosmicode.roomie.domain.AccountRequest;
import com.cosmicode.roomie.domain.AccountResponse;
import com.cosmicode.roomie.domain.LoginRequest;
import com.cosmicode.roomie.domain.LoginResponse;
import com.cosmicode.roomie.domain.Register;

import retrofit2.Call;
import retrofit2.http.*;

public interface UserApiEndpointInterface {

    @POST("authenticate")
    Call<LoginResponse> postLogin(@Body LoginRequest loginRequest);

    @GET("account")
    Call<AccountResponse> getAccount();

    @POST("register")
    Call<Void> postRegister(@Body Register register);

    @POST("account")
    Call<Void> postAccountUpdate(@Body  AccountRequest request);

    @POST("account/change-password")
    Call<Void> postChangePassword(@Body String newPassword);

    @POST("account/reset-password/init")
    Call<Void> postRecoverPassword(String email);

    @POST("authenticate/appFacebook")
    Call<LoginResponse> postLoginFacebook(String token);

    @POST("authenticate/appGoogle")
    Call<LoginResponse> postLoginGoogle(String token);

}
