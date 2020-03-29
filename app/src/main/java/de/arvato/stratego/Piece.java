package de.arvato.stratego;

public class Piece {
    private int player;
    private PieceEnum pieceEnum;
    private boolean pieceDiscovered;

    public PieceEnum getPieceEnum() {
        return pieceEnum;
    }

    public void setPieceEnum(PieceEnum pieceEnum) {
        this.pieceEnum = pieceEnum;
    }

     public int getPlayer() {
        return player;
    }
    public void setPlayer(int player) {
        this.player = player;
    }
    public void setPieceDiscovered(boolean pieceDiscovered) {
        this.pieceDiscovered = pieceDiscovered;
    }
    public boolean isPieceDiscovered() {
        return pieceDiscovered;
    }
}