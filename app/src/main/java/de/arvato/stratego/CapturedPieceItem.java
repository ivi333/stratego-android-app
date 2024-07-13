package de.arvato.stratego;

import de.arvato.stratego.game.PieceEnum;

public class CapturedPieceItem {

    private final PieceEnum pieceEnum;
    private final int player;
    private final int captured;

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
}
