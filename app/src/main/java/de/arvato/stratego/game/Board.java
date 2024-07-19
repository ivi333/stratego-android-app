package de.arvato.stratego.game;

import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.arvato.stratego.StrategoConstants;
import de.arvato.stratego.util.Pos;

public class Board {
    protected Piece [] pieces;
    protected int turn;
    protected Map<PieceEnum, Integer> capturedPiecesRed;
    protected Map<PieceEnum, Integer> capturedPiecesBlue;
    protected StrategoConstants.GameStatus gameStatus;
    protected int countMoves;
    protected List<Integer> possibleBombsHuman;
    protected List<HistoryPiece> historyMoves;

    private static final String TAG ="Board";

    public Board () {
        this.pieces = new Piece [StrategoConstants.BOARD_SIZE];
        this.capturedPiecesBlue = new HashMap<PieceEnum, Integer>();
        this.capturedPiecesRed = new HashMap<PieceEnum, Integer>();
        this.possibleBombsHuman = new ArrayList<Integer>();
        this.historyMoves = new ArrayList<HistoryPiece>();
    }

    public void changeGameStatus (StrategoConstants.GameStatus status) {
        this.gameStatus = status;
    }

    public void initTurn (final int player) {
        this.turn = player;
    }

    public int getTurn () {
        return this.turn;
    }

    public void changeTurn () {
        if (turn == StrategoConstants.RED) {
            turn = StrategoConstants.BLUE;
        } else {
            turn = StrategoConstants.RED;
        }
    }

    public boolean initBoard (int to, int selectPos) {
        int x1, x2;

        // player is always shown down
        x1 = StrategoConstants.DOWN_PLAYER[0];
        x2 = StrategoConstants.DOWN_PLAYER[1];

        if (selectPos != -1 && to >= x1 && to < x2 ) {
            //interchange pos
            Piece tmp = pieces[to];
            pieces[to] = pieces[selectPos];
            pieces[selectPos] = tmp;
            return true;
        }
        return false;
    }

    public boolean move (int to, int selectPos) {
        if (selectPos != -1 && isPlayablePosition(to)) {
            if (!isValidMovement (selectPos, to)) {
                Log.e(TAG, "Move no possible from:" + selectPos + " to:" + to);
                return false;
            }
            Piece pieceFrom = pieces[selectPos];
            Piece pieceTo = pieces[to];

            PieceFightStatus fightStatus = PieceFight.fight(pieceFrom, pieceTo);
            if (fightStatus == PieceFightStatus.NO_FIGHT) {
                pieces[to] = pieces[selectPos];
                pieces[selectPos] = null;
            } else if (fightStatus == PieceFightStatus.TIE) {
                pieces[selectPos] = null;
                pieces[to] = null;
                capturePiece(pieceFrom.getPlayer(), pieceFrom.getPieceEnum());
                capturePiece(pieceTo.getPlayer(), pieceTo.getPieceEnum());
            } else if (fightStatus == PieceFightStatus.FLAG_CAPTURED) {
                pieces[to] = pieces[selectPos];
                pieces[selectPos] = null;
                capturePiece(pieceTo.getPlayer(), pieceTo.getPieceEnum());
                finishGame ();
            } else if (fightStatus == PieceFightStatus.PIECE1_WIN) {
                pieces[to] = pieces[selectPos];
                pieces[selectPos] = null;
                capturePiece(pieceTo.getPlayer(), pieceTo.getPieceEnum());
                pieceFrom.setPieceDiscovered(true);
            } else if (fightStatus == PieceFightStatus.PIECE2_WIN) {
                pieces[selectPos] = null;
                capturePiece(pieceFrom.getPlayer(), pieceFrom.getPieceEnum());
                pieceTo.setPieceDiscovered(true);
            }

            //boolean updatedBomb = updatePossibleBoms (selectPos);
            addHistory (selectPos, to, pieceFrom, pieceTo, fightStatus, getTurn());
            countMoves++;
        } else {
            Log.e(TAG, "Move no possible from:" + selectPos + " to:" + to);
            return false;
        }
        return true;
    }

    //TODO Ivan review
    private void addHistory(int selectPos, int to, Piece pieceFrom, Piece pieceTo, PieceFightStatus fightStatus, int player) {
        HistoryPiece history = new HistoryPiece();
        history.moveFrom = selectPos;
        history.moveTo = to;
        history.pieceFrom = pieceFrom;
        history.pieceTo = pieceTo;
        history.fightStatus = fightStatus;
        history.player = player;
        historyMoves.add(history);
    }

    /*public void undoLastMove () {
        HistoryPiece history = historyMoves.remove(historyMoves.size()-1);
        if (history!=null) {
            pieces[history.moveFrom] = history.pieceFrom;
            pieces[history.moveTo] = history.pieceTo;
            switch (history.fightStatus) {
                case NO_FIGHT:
                    break;
                case PIECE1_WIN:
                    undoCapturePiece (history.pieceTo);
                    break;
                case PIECE2_WIN:
                    undoCapturePiece (history.pieceFrom);
                    break;
                case TIE:
                    undoCapturePiece (history.pieceFrom);
                    undoCapturePiece (history.pieceTo);
                    break;
                case FLAG_CAPTURED:
                    undoCapturePiece (history.pieceTo);
                    break;
            }
            countMoves--;
        }
    }*/

    private void undoCapturePiece(Piece piece) {
        if (piece.getPlayer() == StrategoConstants.RED) {
            if (capturedPiecesRed.containsKey(piece.getPieceEnum())) {
                Integer i = capturedPiecesRed.get(piece.getPieceEnum()) - 1;
                if (i==0) {
                    capturedPiecesRed.remove(piece.getPieceEnum());
                } else {
                    capturedPiecesRed.put(piece.getPieceEnum(), i);
                }
            }
        } else {
            if (capturedPiecesBlue.containsKey(piece.getPieceEnum())) {
                Integer i = capturedPiecesBlue.get(piece.getPieceEnum()) - 1;
                if (i==0) {
                    capturedPiecesBlue.remove(piece.getPieceEnum());
                } else {
                    capturedPiecesBlue.put(piece.getPieceEnum(), i);
                }
            }
        }
    }
    private void capturePiece (final int player, PieceEnum piece) {
        if (StrategoConstants.RED == player) {
            if (capturedPiecesRed.containsKey(piece)) {
                Integer i = capturedPiecesRed.get(piece);
                capturedPiecesRed.put(piece, ++i);
            } else {
                capturedPiecesRed.put(piece, 1);
            }
        } else {
            if (capturedPiecesBlue.containsKey(piece)) {
                Integer i = capturedPiecesBlue.get(piece);
                capturedPiecesBlue.put(piece, ++i);
            } else {
                capturedPiecesBlue.put(piece, 1);
            }
        }
    }

    public void finishGame() {
        changeGameStatus(StrategoConstants.GameStatus.FINISH);
    }

    public Piece getPieceAt(int i) {
        return pieces[i];
    }

    public void setPieceAt (Piece p, int i) {
        pieces[i]=p;
    }

    public boolean isValidMovement (int selectedPos, int pos) {
        List<Integer> possibilities = getPossibleMovements(selectedPos);
        boolean res=false;
        for (Integer x : possibilities) {
            if (x.equals(pos)) {
                res=true;
                break;
            }
        }
        return res;
    }

    public List<Integer> getPossibleMovements (int pos) {
        List<Integer> res = Collections.emptyList();
        switch (gameStatus) {
            case PLAY:
                Piece piece = pieces[pos];
                if (piece == null) {
                    return res;
                }
                if (piece.getPieceEnum().isAllowMovement()) {
                    switch (piece.getPieceEnum()) {
                        case SCOUT:
                            res = calculateMovement (piece, pos, true);
                            break;
                        default:
                            res = calculateMovement (piece, pos, false);
                            break;
                    }
                }
                break;
            default:
                break;
        }
        return res;
    }

    private List<Integer> calculateMovement(final Piece piece, final int pos, final boolean isMultiple) {
        int row = Pos.row(pos);
        int col = Pos.col(pos);
        List<Integer> freePos = new ArrayList<Integer>();
        int nextPos;
        if (isMultiple) {
            int tmpRow = row;
            boolean available=true;
            while ( ((tmpRow -1) >=0) && available) {
                nextPos = Pos.fromColAndRow(col, (tmpRow-1));
                if (nextPos >= 0 && nextPos < StrategoConstants.BOARD_SIZE) {
                    available = nextPositionAvailable(piece, nextPos);
                    if (available) {
                        freePos.add(nextPos);
                    }
                    if (pieces[nextPos] != null) break;
                }
                tmpRow--;
            }

            tmpRow = row;
            available = true;
            while ( ((tmpRow + 1) < 10) && available) {
                nextPos = Pos.fromColAndRow(col, (tmpRow +1));
                if (nextPos >= 0 && nextPos < StrategoConstants.BOARD_SIZE) {
                    available = nextPositionAvailable(piece, nextPos);
                    if (available) {
                        freePos.add(nextPos);
                    }
                    if (pieces[nextPos] != null) break;
                }
                tmpRow++;
            }

            available = true;
            int tmpPos = pos;
            int tmpCol = col;
            while ( (Pos.row(tmpPos+1) == row) && available) {
                nextPos = Pos.fromColAndRow((tmpCol+1), row);
                if (nextPos >= 0 && nextPos < StrategoConstants.BOARD_SIZE) {
                    available = nextPositionAvailable(piece, nextPos);
                    if (available) {
                        freePos.add(nextPos);
                    }
                    if (pieces[nextPos] != null) break;
                }
                tmpPos++;
                tmpCol++;
            }

            tmpPos=pos;
            tmpCol=col;
            available=true;
            while ( (Pos.row(tmpPos-1) == row) && available) {
                nextPos = Pos.fromColAndRow((tmpCol-1), row);
                if (nextPos >= 0 && nextPos < StrategoConstants.BOARD_SIZE) {
                    available = nextPositionAvailable (piece, nextPos);
                    if (available) {
                        freePos.add(nextPos);
                    }
                    if (pieces[nextPos] != null) break;
                }
                tmpPos--;
                tmpCol--;
            }
        } else {
            if ( (row -1) >= 0) {
                nextPos = Pos.fromColAndRow(col, (row -1));
                if (nextPos >= 0 && nextPos < StrategoConstants.BOARD_SIZE) {
                    boolean available = nextPositionAvailable(piece, nextPos);
                    if (available) {
                        freePos.add(nextPos);
                    }
                }
            }

            if ( (row + 1) < 10) {
                nextPos = Pos.fromColAndRow(col, (row +1));
                if (nextPos >= 0 && nextPos < StrategoConstants.BOARD_SIZE) {
                    boolean available = nextPositionAvailable(piece, nextPos);
                    if (available) {
                        freePos.add(nextPos);
                    }
                }
            }

            if ( Pos.row(pos+1) == row ) {
                nextPos = Pos.fromColAndRow((col+1), row);
                if (nextPos >= 0 && nextPos < StrategoConstants.BOARD_SIZE) {
                    boolean available = nextPositionAvailable(piece, nextPos);
                    if (available) {
                        freePos.add(nextPos);
                    }
                }
            }

            if ( Pos.row(pos-1) == row) {
                nextPos = Pos.fromColAndRow((col-1), row);
                if (nextPos >= 0 && nextPos < StrategoConstants.BOARD_SIZE) {
                    boolean available = nextPositionAvailable (piece, nextPos);
                    if (available) {
                        freePos.add(nextPos);
                    }
                }
            }
        }
        return freePos;
    }

    private boolean nextPositionAvailable(Piece piece, int nextPos) {
        // not playable position
        if (!isPlayablePosition(nextPos)) return false;
        // Board position is free
        Piece nextPiece = pieces[nextPos];
        if (nextPiece == null) return true;
        //Opposite piece
        if (nextPiece.getPlayer() != piece.getPlayer()) return true;
        // by default is not playable
        return false;
    }

    public static boolean isPlayablePosition (final int pos) {
        return pos != StrategoConstants.c6 && pos != StrategoConstants.d6
                && pos != StrategoConstants.c5 && pos != StrategoConstants.d5
                && pos != StrategoConstants.g6 && pos != StrategoConstants.h6
                && pos != StrategoConstants.g5 && pos != StrategoConstants.h5;
    }

    public void printPieces () {
        StringBuffer sb = new StringBuffer();
        for (int z=0;z<StrategoConstants.BOARD_SIZE;z++) {
            if (z>0 && z%10 == 0) {
                sb.append("\n");
            }
            sb.append("[");
            if (z<=9) {
                sb.append("0");
            }
            sb.append(z);
            sb.append("]");
            if (pieces[z] != null) {
                sb.append("[");
                sb.append(pieces[z].getPlayer());
                sb.append("]");
                sb.append(pieces[z].getPieceEnum().name().substring(0, 3));
                sb.append("  |  ");
            } else {
                sb.append("    ---    ");
            }
        }
        Log.d(TAG, sb.toString());
    }

    public StrategoConstants.GameStatus getGameStatus() {
        return gameStatus;
    }

    public boolean hasDiscoveredPieces (int player) {
        boolean res=false;
        for (int z=0;z<StrategoConstants.BOARD_SIZE && !res;z++) {
            if (pieces[z]!=null && pieces[z].getPlayer() == player && pieces[z].isPieceDiscovered()) {
                res=true;
            }
        }
        return res;
    }

    public Map<PieceEnum, List<Integer>> getPositionPieces (int player) {
        Map<PieceEnum, List<Integer>> result = new HashMap<>();
        for (int z=0;z<StrategoConstants.BOARD_SIZE;z++) {
            if (pieces[z]!=null && pieces[z].getPlayer() == player) {
                Piece p = pieces[z];
                if (!result.containsKey(p.getPieceEnum())) {
                    result.put(p.getPieceEnum(), new ArrayList<Integer>());
                }
                result.get(p.getPieceEnum()).add(z);
            }
        }
        return result;
    }

    public Map<PieceEnum, Integer> getCapturedPiecesRed() {
        return capturedPiecesRed;
    }

    public Map<PieceEnum, Integer> getCapturedPiecesBlue() {
        return capturedPiecesBlue;
    }



    public void printDiscoveredPieces () {
        for (int z=0;z<StrategoConstants.BOARD_SIZE ;z++) {
            if (pieces[z]!=null && pieces[z].isPieceDiscovered()) {
                Log.d(TAG, "Disc.Piece Pos:" + z + " " + pieces[z]);
            }
        }
    }

    public void printStatus() {
        printPieces();
        printBoard ();
        printDiscoveredPieces();
    }

    public boolean isGameOver() {
        return getGameStatus() == StrategoConstants.GameStatus.FINISH;
    }

    public List<HistoryPiece> getHistoryMoves() {
        return historyMoves;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Board [pieces=");
        builder.append(Arrays.toString(pieces));
        builder.append(", turn=");
        builder.append(turn);
        builder.append(", capturedPiecesRed=");
        builder.append(capturedPiecesRed);
        builder.append(", capturedPiecesBlue=");
        builder.append(capturedPiecesBlue);
        builder.append(", gameStatus=");
        builder.append(gameStatus);
        builder.append(", countMoves=");
        builder.append(countMoves);
        builder.append("]");
        return builder.toString();
    }

    public void printBoard () {
        StringBuilder builder = new StringBuilder();
        builder.append(", \nturn=");
        builder.append(turn);
        builder.append(", \ncapturedPiecesRed=");
        builder.append(capturedPiecesRed);
        builder.append(", \ncapturedPiecesBlue=");
        builder.append(capturedPiecesBlue);
        builder.append(", \ngameStatus=");
        builder.append(gameStatus);
        builder.append("\npossibleHumanBombs=");
        builder.append(possibleBombsHuman);
        builder.append(", \ncountMoves=");
        builder.append(countMoves);
        builder.append("]");
        Log.d(TAG, builder.toString());
    }
}