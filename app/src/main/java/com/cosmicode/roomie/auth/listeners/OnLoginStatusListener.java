package com.cosmicode.roomie.auth.listeners;

public interface OnLoginStatusListener {
    void onLogin(String authToken);
    void onLogout();
}
