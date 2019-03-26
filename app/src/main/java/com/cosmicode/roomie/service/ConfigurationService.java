package com.cosmicode.roomie.service;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.cosmicode.roomie.BaseActivity;
import com.cosmicode.roomie.domain.Address;
import com.cosmicode.roomie.domain.UserPreferences;
import com.cosmicode.roomie.util.network.ApiServiceGenerator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfigurationService {
    private static final String TAG = "ConfigurationService";

    private Context context;
    private String authToken;
    private ConfigurationListener listener;

    public ConfigurationService(Context context, ConfigurationListener listener) {
        this.context = context;
        this.listener = listener;
        this.authToken = ((BaseActivity) this.context).getJhiUsers().getAuthToken();
    }

    public UserPreferences getConfigurationById (Long id){

        ConfigurationApiEndpointInterface apiService = ApiServiceGenerator.createService(ConfigurationApiEndpointInterface.class, authToken);

        Call<UserPreferences> call = apiService.getUserPreferenceById(id);

        call.enqueue(new Callback<UserPreferences>() {
            @Override
            public void onResponse(Call<UserPreferences> call, Response<UserPreferences> response) {
                if (response.code() == 200) { // OK
                    listener.onGetConfigurationSuccess(response.body());

                } else {
                    Log.e(TAG, Integer.toString(response.code()));
                    listener.onConfigurationError("ERROR getting resources");
                }
            }

            @Override
            public void onFailure(Call<UserPreferences> call, Throwable t) {
                Toast.makeText(context, "Something went wrong!",
                        Toast.LENGTH_LONG).show();
                listener.onConfigurationError("Something went wrong!");
            }
        });
        return null;
    }

    public UserPreferences updateConfiguration (UserPreferences userPreferences){

        ConfigurationApiEndpointInterface apiService = ApiServiceGenerator.createService(ConfigurationApiEndpointInterface.class, authToken);

        Call<UserPreferences> call = apiService.updateUserPreference(userPreferences);

        call.enqueue(new Callback<UserPreferences>() {
            @Override
            public void onResponse(Call<UserPreferences> call, Response<UserPreferences> response) {
                if (response.code() == 200) { // OK
                    listener.onUpdateConfigurationSuccess(response.body());

                } else {
                    Log.e(TAG, Integer.toString(response.code()));
                    listener.onConfigurationError("ERROR getting resources");
                }
            }

            @Override
            public void onFailure(Call<UserPreferences> call, Throwable t) {
                Toast.makeText(context, "Something went wrong!",
                        Toast.LENGTH_LONG).show();
                listener.onConfigurationError("Something went wrong!");
            }
        });
        return null;
    }

    public interface ConfigurationListener {
        void onGetConfigurationSuccess(UserPreferences userPreferences);
        void onUpdateConfigurationSuccess(UserPreferences userPreferences);
        void onConfigurationError(String error);
    }
}
