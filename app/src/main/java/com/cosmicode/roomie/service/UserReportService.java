package com.cosmicode.roomie.service;

import android.content.Context;
import android.util.Log;

import com.cosmicode.roomie.BaseActivity;
import com.cosmicode.roomie.domain.UserReport;
import com.cosmicode.roomie.util.network.ApiServiceGenerator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserReportService {
    private static final String TAG = "UserReportService";

    private Context context;
    private String authToken;
    private UserReportListener listener;

    public UserReportService(Context context, UserReportListener listener) {
        this.context = context;
        this.listener = listener;
        this.authToken = ((BaseActivity) this.context).getJhiUsers().getAuthToken();
    }

    public void getUserReportById (Long id){

        UserReportApiEndpointInterface apiService = ApiServiceGenerator.createService(UserReportApiEndpointInterface.class, authToken);

        Call<UserReport> call = apiService.getUserReportById(id);

        call.enqueue(new Callback<UserReport>() {
            @Override
            public void onResponse(Call<UserReport> call, Response<UserReport> response) {
                if (response.code() == 200) { // OK
                    listener.onGetUserReportSuccess(response.body());

                } else {
                    Log.e(TAG, response.toString());
                    listener.onUserReportError(Integer.toString(response.code()));
                }
            }

            @Override
            public void onFailure(Call<UserReport> call, Throwable t) {
                Log.e(TAG, t.toString());
                listener.onUserReportError(t.getMessage());
            }
        });
    }

    public void createUserReport(UserReport userReport) {

        UserReportApiEndpointInterface apiService = ApiServiceGenerator.createService(UserReportApiEndpointInterface.class, authToken);
        Call<UserReport> call = apiService.createUserReport(userReport);


        call.enqueue(new Callback<UserReport>() {
            @Override
            public void onResponse(Call<UserReport> call, Response<UserReport> response) {
                if (response.code() == 201) { // OK
                    listener.onCreateUserReportSuccess(response.body());

                } else {
                    Log.e(TAG, response.toString());
                    listener.onUserReportError(response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<UserReport> call, Throwable t) {
                Log.e(TAG, t.toString());
                listener.onUserReportError(t.getMessage());
            }
        });
    }

    public void updateUserReport (UserReport userPreferences){

        UserReportApiEndpointInterface apiService = ApiServiceGenerator.createService(UserReportApiEndpointInterface.class, authToken);

        Call<UserReport> call = apiService.updateUserReport(userPreferences);

        call.enqueue(new Callback<UserReport>() {
            @Override
            public void onResponse(Call<UserReport> call, Response<UserReport> response) {
                if (response.code() == 200) { // OK
                    listener.onUpdateUserReportSuccess(response.body());

                } else {
                    Log.e(TAG, response.toString());
                    listener.onUserReportError(Integer.toString(response.code()));
                }
            }

            @Override
            public void onFailure(Call<UserReport> call, Throwable t) {
                Log.e(TAG, t.toString());
                listener.onUserReportError(t.getMessage());
            }
        });
    }

    public interface UserReportListener {
        void onGetUserReportSuccess(UserReport userReport);
        void onCreateUserReportSuccess(UserReport userReport);
        void onUpdateUserReportSuccess(UserReport userReport);
        void onUserReportError(String error);
    }
}
