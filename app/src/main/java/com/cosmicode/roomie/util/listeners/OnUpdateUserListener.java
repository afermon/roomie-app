package com.cosmicode.roomie.util.listeners;

import com.cosmicode.roomie.domain.User;

public interface OnUpdateUserListener {
    void onUpdateUserSuccess(User user);
    void onUpdateUserError(String error);
}
