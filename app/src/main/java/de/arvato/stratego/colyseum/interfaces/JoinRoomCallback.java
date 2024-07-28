package de.arvato.stratego.colyseum.interfaces;

import de.arvato.stratego.colyseum.GameState;
import io.colyseus.Room;

public interface JoinRoomCallback {
    void onSuccess(Room<GameState> room);
    void onError(Exception e);
}