package com.cosmicode.roomie.service;

import com.cosmicode.roomie.domain.Roomie;

public interface RoomieInterface {

    Roomie getRoomieById(Long id);

    Roomie updateRoomie(Roomie roomie);
}
