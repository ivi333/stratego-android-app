package de.arvato.stratego.model;

import java.util.Spliterator;

import de.arvato.stratego.StrategoConstants;
import de.arvato.stratego.game.Piece;
import de.arvato.stratego.game.PieceEnum;

public class PiecesView {
    private String pieces;
    public PiecesView(String pieces) {
        this.pieces = pieces;
    }

    public Piece[] transformToPieceModel (Integer color) {
        Piece [] result = new Piece[StrategoConstants.MAX_PIECES];
        String [] splitPieces  = pieces.split(",");
        for (int k=0; k<splitPieces.length;k++) {
            result[k] = new Piece();
            result[k].setPieceEnum(PieceEnum.valueOf(splitPieces[k]));
            result[k].setPlayer(color);
            result[k].setPieceDiscovered(false);
        }
        return result;
    }
}
