package com.cosmicode.roomie.service;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.cosmicode.roomie.BaseActivity;
import com.cosmicode.roomie.domain.RoomExpense;
import com.cosmicode.roomie.util.network.ApiServiceGenerator;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoomExpenseService {
    private static final String TAG = "RoomExpenseService";

    private Context context;
    private String authToken;
    private RoomExpenseService.RoomExpenseServiceListener listener;

    public RoomExpenseService(Context context, RoomExpenseService.RoomExpenseServiceListener listener) {
        this.context = context;
        this.authToken = ((BaseActivity) this.context).getJhiUsers().getAuthToken();
        this.listener = listener;
    }
    public RoomExpense createExpense(RoomExpense roomExpense){

        RoomExpenseApiEndpointInterface apiService = ApiServiceGenerator.createService(RoomExpenseApiEndpointInterface.class, authToken);

        Call<RoomExpense> call = apiService.createExpense(roomExpense);

        call.enqueue(new Callback<RoomExpense>() {
            @Override
            public void onResponse(Call<RoomExpense> call, Response<RoomExpense> response) {
                if (response.code() == 201) { // OK
                    listener.OnCreateExpense(response.body());

                } else {
                    Log.e(TAG, Integer.toString(response.code()));
                    listener.OnGetTaskByRoomError("ERROR getting resources");
                }
            }

            @Override
            public void onFailure(Call<RoomExpense> call, Throwable t) {
                Toast.makeText(context, "Something went wrong!",
                        Toast.LENGTH_LONG).show();
                listener.OnGetTaskByRoomError("Something went wrong!");
            }
        });
        return null;
    }

    public interface RoomExpenseServiceListener {
        void OnCreateExpense(RoomExpense roomExpense);
        void OnUpdateSuccess(RoomExpense roomExpense);
        void OnGetTaskByRoomError(String error);
    }
}
