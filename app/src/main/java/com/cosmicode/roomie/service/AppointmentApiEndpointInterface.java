package com.cosmicode.roomie.service;

import com.cosmicode.roomie.domain.Appointment;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface AppointmentApiEndpointInterface {

    @GET("appointments/{id}")
    Call<Appointment> getAppointmentById(@Path("id") Long id);

    @GET("appointments/roomie")
    Call<List<Appointment>> getAppointmentByRoomie();

    @PUT("appointments")
    Call<Appointment> updateAppointment(@Body Appointment appointment);

    @POST("appointments")
    Call<Appointment> createAppointmet(@Body Appointment appointment);

}
