package com.cosmicode.roomie.service;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.cosmicode.roomie.BaseActivity;
import com.cosmicode.roomie.domain.Roomie;
import com.cosmicode.roomie.util.network.ApiServiceGenerator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoomieService {
    private static final String TAG = "RoomieService";

    private Context context;
    private String authToken;
    private OnGetCurrentRoomieListener listener;

    public RoomieService(Context context, OnGetCurrentRoomieListener listener) {
        this.context = context;
        this.listener = listener;
        this.authToken = ((BaseActivity) this.context).getJhiUsers().getAuthToken();
    }

    public Roomie getCurrentRoomie(){

        RoomieApiEndpointInterface apiService = ApiServiceGenerator.createService(RoomieApiEndpointInterface.class, authToken);

        Call<Roomie> call = apiService.getCurrentRoomie();

        call.enqueue(new Callback<Roomie>() {
            @Override
            public void onResponse(Call<Roomie> call, Response<Roomie> response) {
                if (response.code() == 200) { // OK
                    listener.onGetCurrentRoomieSuccess(response.body());

                } else {
                    Log.e(TAG, Integer.toString(response.code()));
                    listener.onGetCurrentRoomieError("ERROR getting resources");
                }
            }

            @Override
            public void onFailure(Call<Roomie> call, Throwable t) {
                Toast.makeText(context, "Something went wrong!",
                        Toast.LENGTH_LONG).show();
                listener.onGetCurrentRoomieError("Something went wrong!");
            }
        });
        return null;
    }

    public Roomie updateRoomie(Roomie roomie){

        RoomieApiEndpointInterface apiService = ApiServiceGenerator.createService(RoomieApiEndpointInterface.class, authToken);

        Call<Roomie> call = apiService.updateRoomie(roomie);

        call.enqueue(new Callback<Roomie>() {
            @Override
            public void onResponse(Call<Roomie> call, Response<Roomie> response) {
                if (response.code() == 200) { // OK
                    listener.OnUpdateSuccess(response.body());

                } else {
                    Log.e(TAG, Integer.toString(response.code()));
                    listener.onGetCurrentRoomieError("ERROR getting resources");
                }
            }

            @Override
            public void onFailure(Call<Roomie> call, Throwable t) {
                Toast.makeText(context, "Something went wrong!",
                        Toast.LENGTH_LONG).show();
                listener.onGetCurrentRoomieError("Something went wrong!");
            }
        });
        return null;
    }

    public interface OnGetCurrentRoomieListener {
        void onGetCurrentRoomieSuccess(Roomie roomie);
        void onGetCurrentRoomieError(String error);
        void OnUpdateSuccess(Roomie roomie);
    }
}
