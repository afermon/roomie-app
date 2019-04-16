package com.cosmicode.roomie.service;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.cosmicode.roomie.BaseActivity;
import com.cosmicode.roomie.domain.RoomExpense;
import com.cosmicode.roomie.domain.RoomExpenseSplit;
import com.cosmicode.roomie.util.network.ApiServiceGenerator;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoomExpenseSplitService {
    private static final String TAG = "RoomExpenseService";

    private Context context;
    private String authToken;
    private RoomExpenseSplitService.RoomExpenseSplitServiceListener listener;

    public RoomExpenseSplitService(Context context, RoomExpenseSplitService.RoomExpenseSplitServiceListener listener) {
        this.context = context;
        this.authToken = ((BaseActivity) this.context).getJhiUsers().getAuthToken();
        this.listener = listener;
    }
    public List<RoomExpenseSplit> createExpenseSplit(List<RoomExpenseSplit> roomExpenseSplit){

        RoomExpenseSplitApiEndpointInterface apiService = ApiServiceGenerator.createService(RoomExpenseSplitApiEndpointInterface.class, authToken);

        Call<List<RoomExpenseSplit>> call = apiService.createExpenseSplit(roomExpenseSplit);

        call.enqueue(new Callback<List<RoomExpenseSplit>>() {
            @Override
            public void onResponse(Call<List<RoomExpenseSplit>> call, Response<List<RoomExpenseSplit>> response) {
                if (response.code() == 201) { // OK
                    listener.OnCreateRoomExpenseSplitSuccess(response.body());

                } else {
                    Log.e(TAG, Integer.toString(response.code()));
                    listener.OnGetRoomExpenseSplitError("ERROR getting resources");
                }
            }

            @Override
            public void onFailure(Call<List<RoomExpenseSplit>> call, Throwable t) {
                Toast.makeText(context, "Something went wrong!",
                        Toast.LENGTH_LONG).show();
                listener.OnGetRoomExpenseSplitError("Something went wrong!");
            }
        });
        return null;
    }

    public RoomExpense deleteExpenseSplit(List<RoomExpenseSplit> roomExpenseSplit){

        RoomExpenseSplitApiEndpointInterface apiService = ApiServiceGenerator.createService(RoomExpenseSplitApiEndpointInterface.class, authToken);

        Call<Void> call = apiService.deleteExpenseSplit(roomExpenseSplit);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 200) { // OK
                    listener.OnDeleteExpenseSplitSuccess();

                } else {
                    Log.e(TAG, Integer.toString(response.code()));
                    listener.OnGetRoomExpenseSplitError("ERROR getting resources");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Something went wrong!",
                        Toast.LENGTH_LONG).show();
                listener.OnGetRoomExpenseSplitError("Something went wrong!");
            }
        });
        return null;
    }

    public List<RoomExpenseSplit> getSplitExpensesByExxpense(Long id){

        RoomExpenseSplitApiEndpointInterface apiService = ApiServiceGenerator.createService(RoomExpenseSplitApiEndpointInterface.class, authToken);

        Call<List<RoomExpenseSplit>> call = apiService.geSplittExpenseByExpense(id);

        call.enqueue(new Callback<List<RoomExpenseSplit>>() {
            @Override
            public void onResponse(Call<List<RoomExpenseSplit>> call, Response<List<RoomExpenseSplit>> response) {
                if (response.code() == 200) { // OK
                    listener.OnGetSplitExpenseByRoomSuccess(response.body());

                } else {
                    Log.e(TAG, Integer.toString(response.code()));
                    listener.OnGetRoomExpenseSplitError("ERROR getting resources");
                }
            }

            @Override
            public void onFailure(Call<List<RoomExpenseSplit>> call, Throwable t) {
                Toast.makeText(context, "Something went wrong!",
                        Toast.LENGTH_LONG).show();
                listener.OnGetRoomExpenseSplitError("Something went wrong!");
            }
        });
        return null;
    }

    public interface RoomExpenseSplitServiceListener {
        void OnCreateRoomExpenseSplitSuccess(List<RoomExpenseSplit> roomExpenseSplitList);
        void OnGetRoomExpenseSplitError(String error);
        void OnDeleteExpenseSplitSuccess();
        void OnGetSplitExpenseByRoomSuccess(List<RoomExpenseSplit> body);
    }
}
