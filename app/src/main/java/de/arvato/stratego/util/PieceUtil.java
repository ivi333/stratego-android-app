package de.arvato.stratego.util;

import de.arvato.stratego.StrategoConstants;
import de.arvato.stratego.game.Piece;

public class PieceUtil {

    public static String [] transformPiecesToString (Piece []  pieces) {
        if (pieces != null && pieces.length > 0) {
            String [] piecesArrayString = new String [StrategoConstants.MAX_PIECES];
            for (int k=0;k<pieces.length;k++) {
                piecesArrayString[k] = pieces[k].getPieceEnum().name();
            }
            return piecesArrayString;
        }
        return null;
    }

}
