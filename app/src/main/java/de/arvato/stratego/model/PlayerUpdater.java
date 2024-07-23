package de.arvato.stratego.model;

public class PlayerUpdater {

    private PlayerViewModel playerViewModel;

    public PlayerUpdater(PlayerViewModel playerViewModel) {
        this.playerViewModel = playerViewModel;
    }

    public void updateData (String name, String color) {
        PlayerView newPlayer =  new PlayerView (name, color);
        playerViewModel.setPlayerData(newPlayer);
    }

}
