package de.arvato.stratego;

public class StrategoControl {

    protected Piece [] pieces;
    protected boolean myTurn;
    protected int myColor;

    public StrategoControl () {
        myTurn=false;
        myColor = StrategoConstants.RED;
        pieces = new Piece [100];
        putPiece (0, StrategoConstants.RED, StrategoConstants.FLAG);
        putPiece (1, StrategoConstants.RED, StrategoConstants.SPY);
        putPiece (2, StrategoConstants.RED, StrategoConstants.SCOUT);
        putPiece (3, StrategoConstants.RED, StrategoConstants.MINER);
        putPiece (4, StrategoConstants.RED, StrategoConstants.SERGEANT);
        putPiece (5, StrategoConstants.RED, StrategoConstants.LIEUTENANT);
        putPiece (6, StrategoConstants.RED, StrategoConstants.CAPITAN);
        putPiece (7, StrategoConstants.RED, StrategoConstants.MAJOR);
        putPiece (8, StrategoConstants.RED, StrategoConstants.COLONEL);
        putPiece (9, StrategoConstants.RED, StrategoConstants.GENERAL);
        putPiece (10, StrategoConstants.RED, StrategoConstants.MARSHALL);
        putPiece (11, StrategoConstants.RED, StrategoConstants.BOMB);

        for (int z = 12; z<50; z++) {
            putPiece(z, StrategoConstants.RED, StrategoConstants.MINER);
        }

        for (int z = 50; z<100; z++) {
            putPiece(z, StrategoConstants.BLUE, StrategoConstants.BOMB);
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
