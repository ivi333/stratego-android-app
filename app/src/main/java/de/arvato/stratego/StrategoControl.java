package de.arvato.stratego;

public class StrategoControl {

    protected Piece [] pieces;
    protected boolean myTurn;
    protected int myColor;

    public StrategoControl () {
        myTurn=false;
        myColor = StrategoConstants.RED;
        pieces = new Piece [100];
        putPiece (0, StrategoConstants.RED, StrategoConstants.KNIGHT);
        putPiece (1, StrategoConstants.RED, StrategoConstants.QUEEN);
        putPiece (2, StrategoConstants.RED, StrategoConstants.KING);
        putPiece (3, StrategoConstants.RED, StrategoConstants.PAWN);
        for (int z = 4; z<50; z++) {
            putPiece(z, StrategoConstants.RED, StrategoConstants.QUEEN);
        }

        for (int z = 50; z<100; z++) {
            putPiece(z, StrategoConstants.BLUE, StrategoConstants.QUEEN);
        }

    }

    private void putPiece(int pos, int color, int pieceId) {
        pieces [pos] = new Piece();
        pieces [pos].setColor(color);
        pieces [pos].setId(pieceId);
    }

    public Piece getPieceAt(int i) {
        return pieces[i];
    }
}
