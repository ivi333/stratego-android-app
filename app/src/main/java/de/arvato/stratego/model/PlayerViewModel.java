package de.arvato.stratego.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import de.arvato.stratego.colyseum.Player;

public class PlayerViewModel extends ViewModel {

    private final MutableLiveData<PlayerView> playerData = new MutableLiveData<>();

    public MutableLiveData<PlayerView> getPlayerData() {
        return playerData;
    }

    public void setPlayerData (PlayerView player) {
        playerData.setValue(player);
    }

}
