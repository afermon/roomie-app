package com.cosmicode.roomie.service;

import com.cosmicode.roomie.domain.User;
import com.cosmicode.roomie.util.listeners.OnChangePasswordListener;
import com.cosmicode.roomie.util.listeners.OnLoginListener;
import com.cosmicode.roomie.util.listeners.OnLoginStatusListener;
import com.cosmicode.roomie.util.listeners.OnRecoverPasswordRequestListener;
import com.cosmicode.roomie.util.listeners.OnRegisterListener;
import com.cosmicode.roomie.util.listeners.OnUpdateUserListener;
import com.cosmicode.roomie.util.listeners.OnUserAvailableListener;

public interface UserInterface {

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
