package com.cosmicode.roomie.auth;

import com.cosmicode.roomie.auth.dto.User;
import com.cosmicode.roomie.auth.listeners.OnChangePasswordListener;
import com.cosmicode.roomie.auth.listeners.OnLoginListener;
import com.cosmicode.roomie.auth.listeners.OnLoginStatusListener;
import com.cosmicode.roomie.auth.listeners.OnRecoverPasswordRequestListener;
import com.cosmicode.roomie.auth.listeners.OnRegisterListener;
import com.cosmicode.roomie.auth.listeners.OnUpdateUserListener;
import com.cosmicode.roomie.auth.listeners.OnUserAvailableListener;

public interface JhiUsers {

    boolean isLoginSaved();

    boolean isGoogleLoginSaved();

    boolean isFacebookLoginSaved();

    void autoLogin(OnLoginListener listener);

    void login(String login, String password, OnLoginListener listener);
    void register(String email, String firstName, String lastName, String password, OnRegisterListener listener, String phoneNumber);
    void logout();

    void update(User user, OnUpdateUserListener listener);
    void changePassword(String newPassword, OnChangePasswordListener listener);
    void recoverPassword(String email, OnRecoverPasswordRequestListener listener);

    void getLogedUser(OnUserAvailableListener listener);
    String getAuthToken();

    void loginWithFacebook(String token, OnLoginListener listener);

     void loginWithGoogle(String token, OnLoginListener listener);

     void setOnLoginStatusListener(OnLoginStatusListener listener);
}
