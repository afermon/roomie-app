package com.cosmicode.roomie.util.listeners;

import com.cosmicode.roomie.domain.Roomie;

public interface OnGetRoomieByIdListener {

    void OnGetRoomieByIdSuccess(Roomie roomie);

    void onGetRoomieError(String error);
}
