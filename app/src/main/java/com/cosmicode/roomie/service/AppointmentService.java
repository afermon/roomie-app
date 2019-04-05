package com.cosmicode.roomie.service;

import android.content.Context;
import android.util.Log;

import com.cosmicode.roomie.BaseActivity;
import com.cosmicode.roomie.domain.Appointment;
import com.cosmicode.roomie.util.network.ApiServiceGenerator;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppointmentService {
    private static final String TAG = "AppointmentService";

    private Context context;
    private String authToken;
    private OnAppointmentListener listener;

    public AppointmentService(Context context, OnAppointmentListener listener) {
        this.context = context;
        this.listener = listener;
        this.authToken = ((BaseActivity) this.context).getJhiUsers().getAuthToken();
    }

    public void getAppointmentById (Long id){

        AppointmentApiEndpointInterface apiService = ApiServiceGenerator.createService(AppointmentApiEndpointInterface.class, authToken);

        Call<Appointment> call = apiService.getAppointmentById(id);

        call.enqueue(new Callback<Appointment>() {
            @Override
            public void onResponse(Call<Appointment> call, Response<Appointment> response) {
                if (response.code() == 200) { // OK
                    listener.onGetAppointmentSuccess(response.body());

                } else {
                    Log.e(TAG, response.toString());
                    listener.onAppointmentError(Integer.toString(response.code()));
                }
            }

            @Override
            public void onFailure(Call<Appointment> call, Throwable t) {
                Log.e(TAG, t.toString());
                listener.onAppointmentError(t.getMessage());
            }
        });
    }

    public List<Appointment> getAllAppointmentsRoomie() {

        AppointmentApiEndpointInterface apiService = ApiServiceGenerator.createService(AppointmentApiEndpointInterface.class, authToken);

        Call<List<Appointment>> call = apiService.getAppointmentByRoomie();

        call.enqueue(new Callback<List<Appointment>>() {
            @Override
            public void onResponse(Call<List<Appointment>> call, Response<List<Appointment>> response) {
                if (response.code() == 200) { // OK
                    listener.onGetAppointmentListSuccess(response.body());

                } else {
                    Log.e(TAG, response.toString());
                    listener.onAppointmentError(Integer.toString(response.code()));
                }
            }

            @Override
            public void onFailure(Call<List<Appointment>> call, Throwable t) {
                Log.e(TAG, t.toString());
                listener.onAppointmentError(t.getMessage());
            }
        });
        return null;
    }

    public void createAppointment(Appointment appointment) {

        AppointmentApiEndpointInterface apiService = ApiServiceGenerator.createService(AppointmentApiEndpointInterface.class, authToken);
        Call<Appointment> call = apiService.createAppointmet(appointment);


        call.enqueue(new Callback<Appointment>() {
            @Override
            public void onResponse(Call<Appointment> call, Response<Appointment> response) {
                if (response.code() == 201) { // OK
                    listener.onCreateAppointmentSuccess(response.body());

                } else {
                    Log.e(TAG, response.toString());
                    listener.onAppointmentError(response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<Appointment> call, Throwable t) {
                Log.e(TAG, t.toString());
                listener.onAppointmentError(t.getMessage());
            }
        });
    }

    public void updateAppointment (Appointment appointment){

        AppointmentApiEndpointInterface apiService = ApiServiceGenerator.createService(AppointmentApiEndpointInterface.class, authToken);

        Call<Appointment> call = apiService.updateAppointment(appointment);

        call.enqueue(new Callback<Appointment>() {
            @Override
            public void onResponse(Call<Appointment> call, Response<Appointment> response) {
                if (response.code() == 200) { // OK
                    listener.onUpdateAppointmentSuccess(response.body());

                } else {
                    Log.e(TAG, response.toString());
                    listener.onAppointmentError(Integer.toString(response.code()));
                }
            }

            @Override
            public void onFailure(Call<Appointment> call, Throwable t) {
                Log.e(TAG, t.toString());
                listener.onAppointmentError(t.getMessage());
            }
        });
    }

    public interface OnAppointmentListener {
        void onCreateAppointmentSuccess(Appointment appointment);
        void onUpdateAppointmentSuccess(Appointment appointment);
        void onGetAppointmentSuccess(Appointment appointment);
        void onGetAppointmentListSuccess(List<Appointment> appointment);
        void onAppointmentError(String error);
    }
}
