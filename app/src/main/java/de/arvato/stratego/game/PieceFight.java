package de.arvato.stratego.game;
public class PieceFight {

    public static PieceFightStatus fight (Piece pieceFrom, Piece pieceTo) {
        //target board is not a piece
        if (pieceTo == null) {
            return PieceFightStatus.NO_FIGHT;
        } else {
            //same piece
            if (pieceFrom.getPieceEnum() == pieceTo.getPieceEnum()) {
                return PieceFightStatus.TIE;
            } else if (pieceTo.getPieceEnum() == PieceEnum.FLAG) {
                return PieceFightStatus.FLAG_CAPTURED;
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
                    default:
                        break;
                }

                if (specialCasesWinPlayer1) {
                    return PieceFightStatus.PIECE1_WIN;
                } else {
                    //fight one vs one
                    if (pieceFrom.getPieceEnum().getPoints() > pieceTo.getPieceEnum().getPoints()) {
                        return PieceFightStatus.PIECE1_WIN;
                    } else {
                        return PieceFightStatus.PIECE2_WIN;
                    }
                }
            }
        }
    }
}