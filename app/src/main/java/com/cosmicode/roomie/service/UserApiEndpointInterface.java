package com.cosmicode.roomie.service;

import com.cosmicode.roomie.domain.Authorization;
import com.cosmicode.roomie.domain.JhiAccount;
import com.cosmicode.roomie.domain.Register;
import com.cosmicode.roomie.domain.RoomieUser;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface UserApiEndpointInterface {

    @POST("authenticate")
    Call<Authorization> postLogin(@Body Authorization authorization);

    @GET("account")
    Call<JhiAccount> getAccount();

    @POST("register")
    Call<Void> postRegister(@Body Register register);

    @POST("account")
    Call<Void> postAccountUpdate(@Body JhiAccount account);

    @POST("account/change-password")
    Call<Void> postChangePassword(@Body String newPassword);

    @POST("account/reset-password/init")
    Call<Void> postRecoverPassword(@Body String mail);

    @POST("authenticate/facebook")
    Call<Authorization> postLoginFacebook(@Body String token);

    @POST("authenticate/google")
    Call<Authorization> postLoginGoogle(@Body String token);

    @GET("users-email/{email}")
    Call<JhiAccount> findUserByEmail(@Path("email") String email);

    @GET("users-id/{id}")
    Call<JhiAccount> findUserById(@Path("id") Long id);

}
