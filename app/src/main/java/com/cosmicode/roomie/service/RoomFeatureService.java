package com.cosmicode.roomie.service;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.cosmicode.roomie.BaseActivity;
import com.cosmicode.roomie.domain.Room;
import com.cosmicode.roomie.domain.RoomFeature;
import com.cosmicode.roomie.util.network.ApiServiceGenerator;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoomFeatureService {

    private static final String TAG = "RoomFeatureService";

    private Context context;
    private String authToken;
    private OnGetFeaturesListener listener;

    public RoomFeatureService(Context context, OnGetFeaturesListener listener) {
        this.context = context;
        this.listener = listener;
        this.authToken = ((BaseActivity) this.context).getJhiUsers().getAuthToken();
    }

    public void getAll(){

        RoomFeatureApiEndpointInterface apiService = ApiServiceGenerator.createService(RoomFeatureApiEndpointInterface.class, authToken);

        Call<List<RoomFeature>> call = apiService.getAllFeatures();

        call.enqueue(new Callback<List<RoomFeature>>() {
            @Override
            public void onResponse(Call<List<RoomFeature>> call, Response<List<RoomFeature>> response) {
                if (response.code() == 200) { // OK
                    listener.onGetFeaturesSuccess(response.body());

                } else {
                    Log.e(TAG, Integer.toString(response.code()));
                    listener.onGetFeaturesError("ERROR getting resources");
                }
            }

            @Override
            public void onFailure(Call<List<RoomFeature>> call, Throwable t) {
                Toast.makeText(context, "Something went wrong!",
                        Toast.LENGTH_LONG).show();
                listener.onGetFeaturesError("Something went wrong!");
            }
        });

    }



    public interface OnGetFeaturesListener {
        void onGetFeaturesSuccess(List<RoomFeature> featureList);
        void onGetFeaturesError(String error);
    }
}
