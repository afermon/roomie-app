package com.cosmicode.roomie.service;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.cosmicode.roomie.BaseActivity;
import com.cosmicode.roomie.domain.Notification;
import com.cosmicode.roomie.util.network.ApiServiceGenerator;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationService {
    private final static String TAG = "NotificationService";
    private Context context;
    private String authToken;
    private NotificationServiceListener listener;

    public NotificationService(Context context, NotificationServiceListener listener) {
        this.context = context;
        this.listener = listener;
        this.authToken = ((BaseActivity) this.context).getJhiUsers().getAuthToken();
    }

    public List<Notification> getAllNotifications(){

        NotificationApiEndpointInterface apiService = ApiServiceGenerator.createService(NotificationApiEndpointInterface.class, authToken);

        Call<List<Notification>> call = apiService.getNotifications();

        call.enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                if (response.code() == 200) { // OK
                    listener.OnGetNotificationsSuccess(response.body());

                } else {
                    Log.e(TAG, Integer.toString(response.code()));
                    listener.OnGetNotificationsError("ERROR getting resources");
                }
            }

            @Override
            public void onFailure(Call<List<Notification>> call, Throwable t) {
                Toast.makeText(context, "Something went wrong!",
                        Toast.LENGTH_LONG).show();
                listener.OnGetNotificationsError("Something went wrong!");
            }
        });
        return null;
    }

    public interface NotificationServiceListener {
        void OnGetNotificationsSuccess(List<Notification> notifications);
        void OnGetNotificationsError(String error);
    }

}
