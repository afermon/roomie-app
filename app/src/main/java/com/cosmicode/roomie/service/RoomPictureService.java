package com.cosmicode.roomie.service;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.cosmicode.roomie.BaseActivity;
import com.cosmicode.roomie.domain.Address;
import com.cosmicode.roomie.domain.RoomPicture;
import com.cosmicode.roomie.util.network.ApiServiceGenerator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoomPictureService {
    private static final String TAG = "RoomPictureService";

    private Context context;
    private String authToken;
    private OnCreatePictureListener listener;

    public RoomPictureService(Context context, OnCreatePictureListener listener) {
        this.context = context;
        this.listener = listener;
        this.authToken = ((BaseActivity) this.context).getJhiUsers().getAuthToken();
    }

    public void createPic (RoomPicture roomPicture){


        RoomPictureApiEnpointInterface apiService = ApiServiceGenerator.createService(RoomPictureApiEnpointInterface.class, authToken);

        Call<RoomPicture> call = apiService.createPicture(roomPicture);

        call.enqueue(new Callback<RoomPicture>() {
            @Override
            public void onResponse(Call<RoomPicture> call, Response<RoomPicture> response) {
                if (response.code() == 201) { // OK
                    listener.onCreatePicSuccess(response.body());
                } else {
                    Log.e(TAG, Integer.toString(response.code()));
                    listener.onPictureError("ERROR getting resources");
                }
            }

            @Override
            public void onFailure(Call<RoomPicture> call, Throwable t) {
                Toast.makeText(context, "Something went wrong!",
                        Toast.LENGTH_LONG).show();
                listener.onPictureError("Something went wrong!");
            }
        });
    }

    public interface OnCreatePictureListener {
        void onCreatePicSuccess(RoomPicture picture);
        void onPictureError(String error);
    }
}
