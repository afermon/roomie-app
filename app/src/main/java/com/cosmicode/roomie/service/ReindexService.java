package com.cosmicode.roomie.service;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.cosmicode.roomie.BaseActivity;
import com.cosmicode.roomie.domain.Address;
import com.cosmicode.roomie.util.network.ApiServiceGenerator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReindexService {
    private static final String TAG = "ReindexService";

    private Context context;
    private String authToken;
    private ReindexListener listener;

    public ReindexService(Context context, ReindexListener listener) {
        this.context = context;
        this.listener = listener;
        this.authToken = ((BaseActivity) this.context).getJhiUsers().getAuthToken();
    }

    public void reindexRoom (Long id){

        ReindexApiEndpointInterface apiService = ApiServiceGenerator.createService(ReindexApiEndpointInterface.class, authToken);

        Call<Void> call = apiService.reindex(id);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 200) { // OK
                    listener.onReindexSuccess();

                } else {
                    Log.e(TAG, Integer.toString(response.code()));
                    listener.onReindexError("ERROR getting resources");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Something went wrong!",
                        Toast.LENGTH_LONG).show();
                listener.onReindexError("Something went wrong!");
            }
        });
    }

    public interface ReindexListener {
        void onReindexSuccess();
        void onReindexError(String error);
    }
}
