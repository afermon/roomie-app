package com.cosmicode.roomie.auth.listeners;

public interface OnLoginListener {
    void onLoginSuccess();
    void onLoginError(String error);
}
