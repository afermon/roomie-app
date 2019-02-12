package com.cosmicode.roomie.auth.listeners;

public interface OnRegisterListener {
    void onRegisterSuccess();
    void onRegisterError(String error);
}
