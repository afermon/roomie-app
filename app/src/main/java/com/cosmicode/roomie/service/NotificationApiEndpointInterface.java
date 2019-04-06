package com.cosmicode.roomie.service;

import com.cosmicode.roomie.domain.Notification;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface NotificationApiEndpointInterface {

    @GET("notifications/roomie")
    Call<List<Notification>> getNotifications();

}
