package com.cosmicode.roomie.util.listeners;

import com.cosmicode.roomie.domain.JhiAccount;

public interface OnGetUserByIdListener {

    void onGetUserSuccess(JhiAccount user);

    void onGetUserError(String error);
}
