package com.cosmicode.roomie.util.listeners;

import com.cosmicode.roomie.domain.Roomie;

public interface OnCreateRoomieListener {

    void onCreateRoomieSuccess(Roomie roomie);

    void onCreateRoomieError(String error);
}
