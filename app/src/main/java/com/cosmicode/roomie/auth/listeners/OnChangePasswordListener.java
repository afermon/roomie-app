package com.cosmicode.roomie.auth.listeners;

public interface OnChangePasswordListener {
    void onChangePasswordSuccess();
    void onChangePasswordError(String error);
}
