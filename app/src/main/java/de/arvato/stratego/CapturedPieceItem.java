package de.arvato.stratego;

import de.arvato.stratego.game.PieceEnum;

public class CapturedPieceItem {

    private final PieceEnum pieceEnum;
    private int player;
    private int captured;
    private boolean dead;

    public CapturedPieceItem(PieceEnum pieceEnum, int player, int captured) {
        this.pieceEnum = pieceEnum;
        this.player = player;
        this.captured = captured;
    }

    public PieceEnum getPieceEnum() {
        return pieceEnum;
    }

    public int getPlayer() {
        return player;
    }

    public int getCaptured () {
        return captured;
    }

    public void setPlayer(int player) {
        this.player = player;
    }

    public void setCaptured(int captured) {
        this.captured = captured;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }
}
