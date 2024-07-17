package de.arvato.stratego.game;

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

    /*public Piece copy () {
        Piece newPiece = new Piece();
        newPiece.player = this.player;
        newPiece.pieceEnum = this.pieceEnum;
        newPiece.pieceDiscovered = this.pieceDiscovered;
        return newPiece;
    }*/

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Piece [player=");
        builder.append(player);
        builder.append(", pieceEnum=");
        builder.append(pieceEnum);
        builder.append(", pieceDiscovered=");
        builder.append(pieceDiscovered);
        builder.append("]");
        return builder.toString();
    }

}