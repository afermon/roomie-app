package com.cosmicode.roomie.util.listeners;

import com.cosmicode.roomie.domain.Room;

public interface OnPayPremiumListener {

    void onPaySuccess(Room room);

    void onPayError(String error);
}
