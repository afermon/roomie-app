package com.cosmicode.roomie.service;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.cosmicode.roomie.BaseActivity;
import com.cosmicode.roomie.domain.RoomEvent;
import com.cosmicode.roomie.domain.RoomTask;
import com.cosmicode.roomie.util.network.ApiServiceGenerator;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoomEventService {
    private static final String TAG = "RoomEventService";

    private Context context;
    private String authToken;
    private OnRoomEventListener listener;

    public RoomEventService(Context context, OnRoomEventListener listener) {
        this.context = context;
        this.listener = listener;
        this.authToken = ((BaseActivity) this.context).getJhiUsers().getAuthToken();
    }

    public void getRoomEventById (Long id){

        RoomEventApiEndpointInterface apiService = ApiServiceGenerator.createService(RoomEventApiEndpointInterface.class, authToken);

        Call<RoomEvent> call = apiService.getRoomEvent(id);

        call.enqueue(new Callback<RoomEvent>() {
            @Override
            public void onResponse(Call<RoomEvent> call, Response<RoomEvent> response) {
                if (response.code() == 200) { // OK
                    listener.onGetRoomEventSuccess(response.body());

                } else {
                    Log.e(TAG, response.toString());
                    listener.onRoomEventError(Integer.toString(response.code()));
                }
            }

            @Override
            public void onFailure(Call<RoomEvent> call, Throwable t) {
                Log.e(TAG, t.toString());
                listener.onRoomEventError(t.getMessage());
            }
        });
    }

    public List<RoomEvent> getAllRoomEventsRoom(Long roomId) {

        RoomEventApiEndpointInterface apiService = ApiServiceGenerator.createService(RoomEventApiEndpointInterface.class, authToken);

        Call<List<RoomEvent>> call = apiService.getRoomRoomEvents(roomId);

        call.enqueue(new Callback<List<RoomEvent>>() {
            @Override
            public void onResponse(Call<List<RoomEvent>> call, Response<List<RoomEvent>> response) {
                if (response.code() == 200) { // OK
                    listener.onGetRoomEventListSuccess(response.body());

                } else {
                    Log.e(TAG, response.toString());
                    listener.onRoomEventError(Integer.toString(response.code()));
                }
            }

            @Override
            public void onFailure(Call<List<RoomEvent>> call, Throwable t) {
                Log.e(TAG, t.toString());
                listener.onRoomEventError(t.getMessage());
            }
        });
        return null;
    }

    public void createRoomEvent(RoomEvent roomEvent) {

        RoomEventApiEndpointInterface apiService = ApiServiceGenerator.createService(RoomEventApiEndpointInterface.class, authToken);
        Call<RoomEvent> call = apiService.createRoomEvent(roomEvent);


        call.enqueue(new Callback<RoomEvent>() {
            @Override
            public void onResponse(Call<RoomEvent> call, Response<RoomEvent> response) {
                if (response.code() == 201) { // OK
                    listener.onCreateRoomEventSuccess(response.body());

                } else {
                    Log.e(TAG, response.toString());
                    listener.onRoomEventError(response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<RoomEvent> call, Throwable t) {
                Log.e(TAG, t.toString());
                listener.onRoomEventError(t.getMessage());
            }
        });
    }

    public void updateRoomEvent (RoomEvent roomEvent){

        RoomEventApiEndpointInterface apiService = ApiServiceGenerator.createService(RoomEventApiEndpointInterface.class, authToken);

        Call<RoomEvent> call = apiService.updateRoomEvent(roomEvent);

        call.enqueue(new Callback<RoomEvent>() {
            @Override
            public void onResponse(Call<RoomEvent> call, Response<RoomEvent> response) {
                if (response.code() == 200) { // OK
                    listener.onUpdateRoomEventSuccess(response.body());

                } else {
                    Log.e(TAG, response.toString());
                    listener.onRoomEventError(Integer.toString(response.code()));
                }
            }

            @Override
            public void onFailure(Call<RoomEvent> call, Throwable t) {
                Log.e(TAG, t.toString());
                listener.onRoomEventError(t.getMessage());
            }
        });
    }

    public RoomTask deleteRoomEvent(Long id){

        RoomEventApiEndpointInterface apiService = ApiServiceGenerator.createService(RoomEventApiEndpointInterface.class, authToken);

        Call<Void> call = apiService.deleteRoomEvent(id);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 200) { // OK
                    listener.onDeleteRoomEventSuccess();
                } else {
                    Log.e(TAG, response.toString());
                    listener.onRoomEventError(Integer.toString(response.code()));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, t.toString());
                listener.onRoomEventError(t.getMessage());
            }
        });
        return null;
    }

    public interface OnRoomEventListener {
        void onCreateRoomEventSuccess(RoomEvent roomEvent);
        void onUpdateRoomEventSuccess(RoomEvent roomEvent);
        void onDeleteRoomEventSuccess();
        void onGetRoomEventSuccess(RoomEvent roomEvent);
        void onGetRoomEventListSuccess(List<RoomEvent> roomEvent);
        void onRoomEventError(String error);
    }
}
