package de.arvato.stratego;

public class StrategoControl {

    private Piece [] pieces;

    public StrategoControl () {
        pieces = new Piece [100];
        putPiece (0, StrategoConstants.RED, StrategoConstants.KNIGHT);
    }

    private void putPiece(int pos, int color, int pieceId) {
        pieces [pos] = new Piece();
        pieces [pos].setColor(color);
        pieces [pos].setId(pieceId);
    }

    public void getPieceAt(int i) {

    }
}
