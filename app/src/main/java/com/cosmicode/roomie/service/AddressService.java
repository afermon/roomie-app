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

public class AddressService {
    private static final String TAG = "AddressService";

    private Context context;
    private String authToken;
    private OnGetAdrressByIdListener listener;

    public AddressService(Context context, OnGetAdrressByIdListener listener) {
        this.context = context;
        this.listener = listener;
        this.authToken = ((BaseActivity) this.context).getJhiUsers().getAuthToken();
    }

    public Address getAddresById (Long id){

        AddressApiEndpointInterface apiService = ApiServiceGenerator.createService(AddressApiEndpointInterface.class, authToken);

        Call<Address> call = apiService.getAddressById(id);

        call.enqueue(new Callback<Address>() {
            @Override
            public void onResponse(Call<Address> call, Response<Address> response) {
                if (response.code() == 200) { // OK
                    listener.onGetAddressByIdSuccess(response.body());

                } else {
                    Log.e(TAG, Integer.toString(response.code()));
                    listener.onGetAddressByIdError("ERROR getting resources");
                }
            }

            @Override
            public void onFailure(Call<Address> call, Throwable t) {
                Toast.makeText(context, "Something went wrong!",
                        Toast.LENGTH_LONG).show();
                listener.onGetAddressByIdError("Something went wrong!");
            }
        });
        return null;
    }
    public interface OnGetAdrressByIdListener {
        void onGetAddressByIdSuccess(Address address);
        void onGetAddressByIdError(String error);
    }
}
