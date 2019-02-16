package com.cosmicode.roomie.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.cosmicode.roomie.domain.LoginRequest;
import com.cosmicode.roomie.domain.User;
import com.cosmicode.roomie.domain.AccountRequest;
import com.cosmicode.roomie.domain.AccountResponse;
import com.cosmicode.roomie.domain.LoginResponse;
import com.cosmicode.roomie.domain.Register;
import com.cosmicode.roomie.util.ApiServiceGenerator;
import com.cosmicode.roomie.util.listeners.OnChangePasswordListener;
import com.cosmicode.roomie.util.listeners.OnLoginListener;
import com.cosmicode.roomie.util.listeners.OnLoginStatusListener;
import com.cosmicode.roomie.util.listeners.OnRecoverPasswordRequestListener;
import com.cosmicode.roomie.util.listeners.OnRegisterListener;
import com.cosmicode.roomie.util.listeners.OnUpdateUserListener;
import com.cosmicode.roomie.util.listeners.OnUserAvailableListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserService implements UserInterface {
    private static final String TAG = "UserService";

    private static final String USERNAME_PREF = "JhiUsername";
    private static final String PASSWORD_PREF = "JhiPassword";
    private static final String GOOGLE_SAVED_PREF = "JhiGoogle";
    private static final String FACEBOOK_SAVED_PREF = "JhiFacebook";

    private final Handler handler;
    private final boolean keepLogedIn;
    private final SharedPreferences preferences;
    private String authToken="";
    private User user;

    private final Object userLock = new Object();
    private boolean gettingUser = false;
    private List<OnUserAvailableListener> userListeners = new ArrayList<>();
    private OnLoginStatusListener statusListener;

    private Context context;

    public static UserInterface with(Context context, String serverUrl, boolean keepLogedIn, SharedPreferences preferences){
        return new UserService(context, keepLogedIn, preferences);
    }

    private UserService(Context context, boolean keepLogedIn, SharedPreferences preferences){
        this.keepLogedIn = keepLogedIn;
        this.preferences = preferences;
        this.handler = new Handler();
        this.context = context;
    }

    @Override
    public void login(final String login, final String password, final OnLoginListener listener) {
        LoginRequest loginRequest = new LoginRequest(login,password);

        UserApiEndpointInterface apiService = ApiServiceGenerator.createService(UserApiEndpointInterface.class);

        Call<LoginResponse> call = apiService.postLogin(loginRequest);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.code() == 200) { // Login OK
                    authToken= response.body().getId_token();

                    listener.onLoginSuccess();
                    if(statusListener!=null){
                        statusListener.onLogin(authToken);
                    }
                    if(keepLogedIn){
                        saveLogin(login,password);
                    }
                    loadUser();

                } else {
                    Log.e("Core","LoginRequest error");
                    listener.onLoginError(response.message());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(context, "Something went wrong!",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveLogin(String login, String password) {
        preferences.edit()
                .putString(USERNAME_PREF,login)
                .putString(PASSWORD_PREF,password)
                .apply();
    }

    @Override
    public boolean isLoginSaved() {
        return preferences.getString(USERNAME_PREF,null)!=null;
    }
    @Override
    public boolean isGoogleLoginSaved(){
        return preferences.getBoolean(GOOGLE_SAVED_PREF,false);
    }
    @Override
    public boolean isFacebookLoginSaved(){
        return preferences.getBoolean(FACEBOOK_SAVED_PREF,false);
    }

    @Override
    public void autoLogin(OnLoginListener listener) {
        login(preferences.getString(USERNAME_PREF,null),preferences.getString(PASSWORD_PREF,null),listener);
    }

    private void loadUser() {
        synchronized (userLock) {
            if(gettingUser) {
                return;
            }
            gettingUser=true;

            UserApiEndpointInterface apiService = ApiServiceGenerator.createService(UserApiEndpointInterface.class, authToken);

            Call<AccountResponse> call = apiService.getAccount();

            call.enqueue(new Callback<AccountResponse>() {
                @Override
                public void onResponse(Call<AccountResponse> call, Response<AccountResponse> response) {
                    if (response.code() == 200) { // Login OK
                        onUserResponse(response.body());

                    } else {
                        synchronized (userLock) {
                            gettingUser = false;
                        }
                    }
                }

                @Override
                public void onFailure(Call<AccountResponse> call, Throwable t) {
                    Toast.makeText(context, "Something went wrong!",
                            Toast.LENGTH_LONG).show();
                    synchronized (userLock) {
                        gettingUser = false;
                    }
                }
            });
        }
    }

    @Override
    public void register(String email, String firstName, String lastName, String password, final OnRegisterListener listener, String phoneNumber) {
        Register register = new Register(email,firstName,lastName,email, phoneNumber, password, Locale.getDefault().getLanguage());

        UserApiEndpointInterface apiService = ApiServiceGenerator.createService(UserApiEndpointInterface.class);

        Call<Void> call = apiService.postRegister(register);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 200) { // Login OK
                    listener.onRegisterSuccess();

                } else {
                    Log.e(TAG,"Register error");
                    listener.onRegisterError(response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Something went wrong!",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void logout() {
        synchronized (userLock){
            user = null;
            gettingUser = false;
        }
        authToken = null;
        if(statusListener!=null){
            statusListener.onLogout();
        }
        saveLogin(null,null);
        preferences.edit().putBoolean(GOOGLE_SAVED_PREF,false).putBoolean(FACEBOOK_SAVED_PREF,false).apply();
    }

    @Override
    public void update(final User user, final OnUpdateUserListener listener) {
        AccountRequest request = new AccountRequest(true,user.getEmail(),user.getFirstName(),null, Locale.getDefault().getLanguage(), user.getLastName(),user.getLogin());

        UserApiEndpointInterface apiService = ApiServiceGenerator.createService(UserApiEndpointInterface.class, authToken);

        Call<Void> call = apiService.postAccountUpdate(request);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 200) { // OK
                    synchronized (userLock){
                        UserService.this.user = user;
                    }
                    listener.onUpdateUserSuccess(user);

                } else {
                    Log.e(TAG,"Update error");
                    listener.onUpdateUserError("");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Something went wrong!",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void changePassword(String newPassword, final OnChangePasswordListener listener) {

        UserApiEndpointInterface apiService = ApiServiceGenerator.createService(UserApiEndpointInterface.class, authToken);

        Call<Void> call = apiService.postChangePassword(newPassword);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 200) { // OK
                    listener.onChangePasswordSuccess();
                } else {
                    Log.e(TAG,"Password error");
                    listener.onChangePasswordError("");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Something went wrong!",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void recoverPassword(String email, final OnRecoverPasswordRequestListener listener) {

        UserApiEndpointInterface apiService = ApiServiceGenerator.createService(UserApiEndpointInterface.class);

        Call<Void> call = apiService.postRecoverPassword(email);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 200) { // OK
                    listener.onRecoverPasswordSuccess();
                } else {
                    Log.e(TAG,"Recover error");
                    listener.onRecoverPasswordError("");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Something went wrong!",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void getLogedUser(OnUserAvailableListener listener) {
        synchronized (userLock){
            if(user!=null){
                listener.onUserAvailable(user);
            }
            else{
                loadUser();
                userListeners.add(listener);
            }
        }
    }

    @Override
    public String getAuthToken() {
        return authToken;
    }

    @Override
    public void loginWithFacebook(String token, final OnLoginListener listener) {

        UserApiEndpointInterface apiService = ApiServiceGenerator.createService(UserApiEndpointInterface.class);

        Call<LoginResponse> call = apiService.postLoginFacebook(token);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.code() == 200) { // Login OK
                    authToken= response.body().getId_token();

                    listener.onLoginSuccess();
                    if(statusListener!=null){
                        statusListener.onLogin(authToken);
                    }
                    if(keepLogedIn) {
                        saveFacebookLogin();
                    }
                    loadUser();

                } else {
                    Log.e("Core","LoginRequest error");
                    listener.onLoginError(response.message());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(context, "Something went wrong!",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void onUserResponse(AccountResponse response) {
        synchronized (userLock) {
            gettingUser = false;
            user = new User(response.getLogin(),response.getEmail(),response.getFirstName(),response.getLastName(),response.getPhoneNumber());
            for (OnUserAvailableListener listener : userListeners) {
                listener.onUserAvailable(user);
            }
            userListeners.clear();
        }
    }

    @Override
    public void loginWithGoogle(String token, final OnLoginListener listener) {

        UserApiEndpointInterface apiService = ApiServiceGenerator.createService(UserApiEndpointInterface.class);

        Call<LoginResponse> call = apiService.postLoginGoogle(token);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.code() == 200) { // Login OK
                    authToken= response.body().getId_token();

                    listener.onLoginSuccess();
                    if(statusListener!=null){
                        statusListener.onLogin(authToken);
                    }
                    if(keepLogedIn) {
                        saveGoogleLogin();
                    }
                    loadUser();

                } else {
                    Log.e("Core","LoginRequest error");
                    listener.onLoginError(response.message());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(context, "Something went wrong!",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveGoogleLogin() {
        preferences.edit().putBoolean(GOOGLE_SAVED_PREF,true).apply();
    }

    private void saveFacebookLogin() {
        preferences.edit().putBoolean(FACEBOOK_SAVED_PREF,true).apply();
    }

    @Override
    public void setOnLoginStatusListener(OnLoginStatusListener listener) {
        this.statusListener = listener;
    }
}
