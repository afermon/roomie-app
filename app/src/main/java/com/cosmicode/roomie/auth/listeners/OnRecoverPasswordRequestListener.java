package com.cosmicode.roomie.auth.listeners;

public interface OnRecoverPasswordRequestListener {
    void onRecoverPasswordSuccess();
    void onRecoverPasswordError(String error);
}
