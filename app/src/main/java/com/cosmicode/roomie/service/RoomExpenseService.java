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
                    listener.OnCreateExpenseSuccess(response.body());

                } else {
                    Log.e(TAG, Integer.toString(response.code()));
                    listener.OnGetExpenseRoomError("ERROR getting resources");
                }
            }

            @Override
            public void onFailure(Call<RoomExpense> call, Throwable t) {
                Toast.makeText(context, "Something went wrong!",
                        Toast.LENGTH_LONG).show();
                listener.OnGetExpenseRoomError("Something went wrong!");
            }
        });
        return null;
    }

    public List<RoomExpense> getAllExpensesByRoom(Long id){

        RoomExpenseApiEndpointInterface apiService = ApiServiceGenerator.createService(RoomExpenseApiEndpointInterface.class, authToken);

        Call<List<RoomExpense>> call = apiService.getExpenseByRoom(id);

        call.enqueue(new Callback<List<RoomExpense>>() {
            @Override
            public void onResponse(Call<List<RoomExpense>> call, Response<List<RoomExpense>> response) {
                if (response.code() == 200) { // OK
                    listener.OnGetExpenseByRoomSuccess(response.body());

                } else {
                    Log.e(TAG, Integer.toString(response.code()));
                    listener.OnGetExpenseRoomError("ERROR getting resources");
                }
            }

            @Override
            public void onFailure(Call<List<RoomExpense>> call, Throwable t) {
                Toast.makeText(context, "Something went wrong!",
                        Toast.LENGTH_LONG).show();
                listener.OnGetExpenseRoomError("Something went wrong!");
            }
        });
        return null;
    }

    public RoomExpense deleteExpense(Long id){

        RoomExpenseApiEndpointInterface apiService = ApiServiceGenerator.createService(RoomExpenseApiEndpointInterface.class, authToken);

        Call<Void> call = apiService.deleteExpense(id);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 200) { // OK
                    listener.OnDeleteSuccess();

                } else {
                    Log.e(TAG, Integer.toString(response.code()));
                    listener.OnGetExpenseRoomError("ERROR getting resources");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Something went wrong!",
                        Toast.LENGTH_LONG).show();
                listener.OnGetExpenseRoomError("Something went wrong!");
            }
        });
        return null;
    }

    public RoomExpense updateExpense(RoomExpense roomTask){

        RoomExpenseApiEndpointInterface apiService = ApiServiceGenerator.createService(RoomExpenseApiEndpointInterface.class, authToken);

        Call<RoomExpense> call = apiService.editExpense(roomTask);

        call.enqueue(new Callback<RoomExpense>() {
            @Override
            public void onResponse(Call<RoomExpense> call, Response<RoomExpense> response) {
                if (response.code() == 200) { // OK
                    listener.OnUpdateSuccess(response.body());

                } else {
                    Log.e(TAG, Integer.toString(response.code()));
                    listener.OnGetExpenseRoomError("ERROR getting resources");
                }
            }

            @Override
            public void onFailure(Call<RoomExpense> call, Throwable t) {
                Toast.makeText(context, "Something went wrong!",
                        Toast.LENGTH_LONG).show();
                listener.OnGetExpenseRoomError("Something went wrong!");
            }
        });
        return null;
    }
    public interface RoomExpenseServiceListener {
        void OnGetExpenseByRoomSuccess(List<RoomExpense> roomTasks);
        void OnCreateExpenseSuccess(RoomExpense roomExpense);
        void OnUpdateSuccess(RoomExpense roomExpense);
        void OnDeleteSuccess();
        void OnGetExpenseRoomError(String error);
    }
}
