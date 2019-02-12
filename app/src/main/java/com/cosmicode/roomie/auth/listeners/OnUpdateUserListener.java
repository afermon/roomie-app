package com.cosmicode.roomie.auth.listeners;

import com.cosmicode.roomie.auth.dto.User;

public interface OnUpdateUserListener {
    void onUpdateUserSuccess(User user);
    void onUpdateUserError(String error);
}
