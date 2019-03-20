package com.cosmicode.roomie.service;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.cosmicode.roomie.BaseActivity;
import com.cosmicode.roomie.domain.RoomTask;
import com.cosmicode.roomie.util.network.ApiServiceGenerator;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoomTaskService {
    private static final String TAG = "RoomTaskService";

    private Context context;
    private String authToken;
    private RoomTaskServiceListener listener;

    public RoomTaskService(Context context, RoomTaskServiceListener listener) {
        this.context = context;
        this.authToken = ((BaseActivity) this.context).getJhiUsers().getAuthToken();
        this.listener = listener;
    }

    public List<RoomTask> getAllTaskByRoom(Long id){

       RoomTasksApiEndpointInterface apiService = ApiServiceGenerator.createService(RoomTasksApiEndpointInterface.class, authToken);

        Call<List<RoomTask>> call = apiService.getTaskByRoom(id);

        call.enqueue(new Callback<List<RoomTask>>() {
            @Override
            public void onResponse(Call<List<RoomTask>> call, Response<List<RoomTask>> response) {
                if (response.code() == 200) { // OK
                    listener.OnGetTaskByRoomSuccess(response.body());

                } else {
                    Log.e(TAG, Integer.toString(response.code()));
                    listener.OnGetTaskByRoomError("ERROR getting resources");
                }
            }

            @Override
            public void onFailure(Call<List<RoomTask>> call, Throwable t) {
                Toast.makeText(context, "Something went wrong!",
                        Toast.LENGTH_LONG).show();
                listener.OnGetTaskByRoomError("Something went wrong!");
            }
        });
        return null;
    }

    public RoomTask createTask(RoomTask roomTask){

        RoomTasksApiEndpointInterface apiService = ApiServiceGenerator.createService(RoomTasksApiEndpointInterface.class, authToken);

        Call<RoomTask> call = apiService.createTask(roomTask);

        call.enqueue(new Callback<RoomTask>() {
            @Override
            public void onResponse(Call<RoomTask> call, Response<RoomTask> response) {
                if (response.code() == 201) { // OK
                    listener.OnCreateTask(response.body());

                } else {
                    Log.e(TAG, Integer.toString(response.code()));
                    listener.OnGetTaskByRoomError("ERROR getting resources");
                }
            }

            @Override
            public void onFailure(Call<RoomTask> call, Throwable t) {
                Toast.makeText(context, "Something went wrong!",
                        Toast.LENGTH_LONG).show();
                listener.OnGetTaskByRoomError("Something went wrong!");
            }
        });
        return null;
    }

    public RoomTask updateTask(RoomTask roomTask){

        RoomTasksApiEndpointInterface apiService = ApiServiceGenerator.createService(RoomTasksApiEndpointInterface.class, authToken);

        Call<RoomTask> call = apiService.editTask(roomTask);

        call.enqueue(new Callback<RoomTask>() {
            @Override
            public void onResponse(Call<RoomTask> call, Response<RoomTask> response) {
                if (response.code() == 200) { // OK
                    listener.OnUpdateSuccess(response.body());

                } else {
                    Log.e(TAG, Integer.toString(response.code()));
                    listener.OnGetTaskByRoomError("ERROR getting resources");
                }
            }

            @Override
            public void onFailure(Call<RoomTask> call, Throwable t) {
                Toast.makeText(context, "Something went wrong!",
                        Toast.LENGTH_LONG).show();
                listener.OnGetTaskByRoomError("Something went wrong!");
            }
        });
        return null;
    }

    public RoomTask deleteTask(Long id){

        RoomTasksApiEndpointInterface apiService = ApiServiceGenerator.createService(RoomTasksApiEndpointInterface.class, authToken);

        Call<Void> call = apiService.deleteTask(id);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 200) { // OK
                    listener.OnDeleteSuccess();

                } else {
                    Log.e(TAG, Integer.toString(response.code()));
                    listener.OnGetTaskByRoomError("ERROR getting resources");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Something went wrong!",
                        Toast.LENGTH_LONG).show();
                listener.OnGetTaskByRoomError("Something went wrong!");
            }
        });
        return null;
    }
    public interface RoomTaskServiceListener {
        void OnCreateTask(RoomTask roomTask);
        void OnUpdateSuccess(RoomTask roomTask);
        void OnDeleteSuccess();
        void OnGetTaskByRoomSuccess(List<RoomTask> roomTasks);
        void OnGetTaskByRoomError(String error);
    }
}
