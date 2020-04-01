package de.arvato.stratego;

public class PieceFight {

    enum PieceFlighStatus {
        NO_FIGHT, PIECE1_WIN, PIECE2_WIN, TIE, FLAG_CAPTURED
    }
    public static PieceFlighStatus fight (Piece pieceFrom, Piece pieceTo) {
        //target board is not a piece
        if (pieceTo == null) {
            return PieceFlighStatus.NO_FIGHT;
        } else {
            //same piece
            if (pieceFrom.getPieceEnum() == pieceTo.getPieceEnum()) {
                return PieceFlighStatus.TIE;
            } else if (pieceTo.getPieceEnum() == PieceEnum.FLAG) {
                return PieceFlighStatus.FLAG_CAPTURED;
            } else {
                boolean specialCasesWinPlayer1 = false;
                switch (pieceFrom.getPieceEnum()) {
                    case SPY:
                        if (pieceTo.getPieceEnum() == PieceEnum.MARSHALL) {
                            specialCasesWinPlayer1=true;
                        }
                        break;
                    case MINER:
                        if (pieceTo.getPieceEnum() == PieceEnum.BOMB) {
                            specialCasesWinPlayer1=true;
                        }
                }

                if (specialCasesWinPlayer1) {
                    return PieceFlighStatus.PIECE1_WIN;
                } else {
                    //fight one vs one
                    if (pieceFrom.getPieceEnum().getPoints() > pieceTo.getPieceEnum().getPoints()) {
                        return PieceFlighStatus.PIECE1_WIN;
                    } else {
                        return PieceFlighStatus.PIECE2_WIN;
                    }
                }
            }
        }
    }
}
