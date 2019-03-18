package com.cosmicode.roomie.service;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.cosmicode.roomie.BaseActivity;
import com.cosmicode.roomie.domain.Room;
import com.cosmicode.roomie.util.network.ApiServiceGenerator;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoomService {
    private final static String TAG = "RoomService";
    private Context context;
    private String authToken;
    private RoomServiceListener listener;

    public RoomService(Context context, RoomServiceListener listener) {
        this.context = context;
        this.listener = listener;
        this.authToken = ((BaseActivity) this.context).getJhiUsers().getAuthToken();
    }

    public List<Room> getAllRooms(){

        RoomApiEndpointInterface apiService = ApiServiceGenerator.createService(RoomApiEndpointInterface.class, authToken);

        Call<List<Room>> call = apiService.getRooms();

        call.enqueue(new Callback<List<Room>>() {
            @Override
            public void onResponse(Call<List<Room>> call, Response<List<Room>> response) {
                if (response.code() == 200) { // OK
                    listener.OnGetRoomsSuccess(response.body());

                } else {
                    Log.e(TAG, response.toString());
                    listener.OnGetRoomsError(Integer.toString(response.code()));
                }
            }

            @Override
            public void onFailure(Call<List<Room>> call, Throwable t) {
                Log.e(TAG, t.toString());
                listener.OnGetRoomsError(t.getMessage());
            }
        });
        return null;
    }

    public void serachRooms(String query){
        RoomApiEndpointInterface apiService = ApiServiceGenerator.createService(RoomApiEndpointInterface.class, authToken);

        Call<List<Room>> call = apiService.serachRooms(query);

        call.enqueue(new Callback<List<Room>>() {
            @Override
            public void onResponse(Call<List<Room>> call, Response<List<Room>> response) {
                if (response.code() == 200) { // OK
                    listener.OnGetRoomsSuccess(response.body());
                } else {
                    Log.e(TAG, response.toString());
                    listener.OnGetRoomsError(Integer.toString(response.code()));
                }
            }

            @Override
            public void onFailure(Call<List<Room>> call, Throwable t) {
                Log.e(TAG, t.toString());
                listener.OnGetRoomsError(t.getMessage());
            }
        });

    }

    public interface RoomServiceListener {
        void OnGetRoomsSuccess(List<Room> rooms);
        void OnGetRoomsError(String error);
    }

}
