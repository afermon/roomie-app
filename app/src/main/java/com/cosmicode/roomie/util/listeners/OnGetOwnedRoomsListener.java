package com.cosmicode.roomie.util.listeners;

import com.cosmicode.roomie.domain.Room;

import java.util.List;

public interface OnGetOwnedRoomsListener {

    void onGetOwnedRoomsSuccess(List<Room> rooms);

    void onGetOwnedRoomsError(String error);
}
