package de.arvato.stratego;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import de.arvato.stratego.util.Pos;

public class StrategoControl {

    private static final Random rng = new Random();

    public static final boolean fake = true;

    protected Piece [] pieces;
    protected boolean myTurn;
    protected int myColor;
    protected int turn;
    protected int selectPos;


    public StrategoControl () {
        startGame ();
    }

    public void startGame () {
        myTurn=false;
        myColor = StrategoConstants.RED;
        pieces = new Piece [100];
        if (!fake) {
            randomPieces(StrategoConstants.RED);
            randomPieces(StrategoConstants.BLUE);
        } else {
            randomFake ();
        }
        turn = StrategoConstants.RED;
    }

    public void movePiece (int to) {
        if (selectPos != -1 && isPlayablePosition(to)) {
            if (!isValidMovement (selectPos, to)) {
                return;
            }
            Piece pieceFrom = pieces[selectPos];
            Piece pieceTo = pieces[to];

            //target board is not a piece
            if (pieceTo == null) {
                pieces[to] = pieces[selectPos];
                pieces[selectPos] = null;
            } else {
                //same piece
                if (pieceFrom.getPieceEnum() == pieceTo.getPieceEnum()) {
                  pieces[selectPos] = null;
                  pieces[to] = null;
                } else if (pieceTo.getPieceEnum() == PieceEnum.FLAG) {
                    pieces[to] = pieces[selectPos];
                    pieces[selectPos] = null;
                    finishGame ();
                } else {
                    boolean specialCasesWinPlayer1 = false;
                    // lets fight
                    //special case
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
                        pieces[to] = pieces[selectPos];
                        pieces[selectPos] = null;
                    } else {
                        //fight one vs one
                        if (pieceFrom.getPieceEnum().getPoints() > pieceTo.getPieceEnum().getPoints()) {
                            pieces[to] = pieces[selectPos];
                            pieces[selectPos] = null;
                        } else {
                            pieces[selectPos] = null;
                        }
                    }
                }
            }
            selectPos = -1;
            changeTurn ();
        }
    }

    private void finishGame() {
        //TODO
    }

    private void changeTurn() {
        if (turn == StrategoConstants.RED) {
            turn = StrategoConstants.BLUE;
        } else {
            turn = StrategoConstants.RED;
        }
    }

    public boolean selectPiece (int pos) {
        Piece piece = pieces[pos];
        if (piece != null && piece.getPlayer() == turn) {
            selectPos = pos;
            return true;
        }
        return false;
    }

    public boolean isValidMovement (int selectedPos, int pos) {
        int [] possibilities = getPossibleMovements(selectedPos);
        boolean res=false;
        for (int x : possibilities) {
            if (x == pos) {
                res=true;
                break;
            }
        }
        return res;
    }

    public int [] getPossibleMovements (int pos) {
        Piece piece = pieces[pos];
        if (piece == null) {
            return new int[0];
        }
        List<Integer> res = Collections.emptyList();
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
        int arrayInt [] = new int[res.size()];
        for (int z=0;z<res.size();z++) {
            arrayInt[z]=res.get(z);
        }
       return arrayInt;
    }

    private List<Integer> calculateMovement(final Piece piece, final int pos, final boolean isMultiple) {
        int row = Pos.row(pos);
        int col = Pos.col(pos);
        List<Integer> freePos = new ArrayList<Integer>();
        int nextPos;
        if (isMultiple) {
            //SCOUT TODO


        } else {
            if ( (row -1) >= 0) {
                nextPos = Pos.fromColAndRow(col, (row -1));
                if (nextPos >= 0 && nextPos < 100) {
                    boolean available = nextPositionAvailable(piece, nextPos);
                    if (available) {
                        freePos.add(nextPos);
                    }
                }
            }

            if ( (row + 1) < 10) {
                nextPos = Pos.fromColAndRow(col, (row +1));
                if (nextPos >= 0 && nextPos < 100) {
                    boolean available = nextPositionAvailable(piece, nextPos);
                    if (available) {
                        freePos.add(nextPos);
                    }
                }
            }

            if ( Pos.row(pos+1) == row ) {
                nextPos = Pos.fromColAndRow((col+1), row);
                if (nextPos >= 0 && nextPos < 100) {
                    boolean available = nextPositionAvailable(piece, nextPos);
                    if (available) {
                        freePos.add(nextPos);
                    }
                }
            }

            if ( Pos.row(pos-1) == row) {
                nextPos = Pos.fromColAndRow((col-1), row);
                if (nextPos >= 0 && nextPos < 100) {
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

    private void randomFake () {
        final Set<Integer> generated = new LinkedHashSet<Integer>();
        Integer next;
        while (generated.size() < StrategoConstants.MAX_PIECES) {
            next = StrategoControl.randomBetween(0, 100);
            if (isPlayablePosition(next))
                generated.add(next);
        }

        List<Integer> boardPosList = new ArrayList<Integer>(generated.size());
        boardPosList.addAll(generated);

        Map<PieceEnum, Integer> mapBoard = new HashMap<PieceEnum, Integer>();
        mapBoard.put(PieceEnum.MARSHALL, StrategoConstants.MAR_MAX);
        mapBoard.put(PieceEnum.GENERAL, StrategoConstants.GEN_MAX);
        mapBoard.put(PieceEnum.COLONEL, StrategoConstants.COR_MAX);
        mapBoard.put(PieceEnum.MAJOR, StrategoConstants.COM_MAX);
        mapBoard.put(PieceEnum.CAPITAN, StrategoConstants.CAP_MAX);
        mapBoard.put(PieceEnum.LIEUTENANT, StrategoConstants.TEN_MAX);
        mapBoard.put(PieceEnum.SERGEANT, StrategoConstants.SAR_MAX);
        mapBoard.put(PieceEnum.MINER, StrategoConstants.MIN_MAX);
        mapBoard.put(PieceEnum.SCOUT, StrategoConstants.EXP_MAX);
        mapBoard.put(PieceEnum.SPY, StrategoConstants.ESP_MAX);
        mapBoard.put(PieceEnum.BOMB, StrategoConstants.BOM_MAX);
        mapBoard.put(PieceEnum.FLAG, StrategoConstants.BAN_MAX);

        int k=0;
        for (Map.Entry<PieceEnum, Integer> entry : mapBoard.entrySet()) {
            PieceEnum piece = entry.getKey();
            for (int j=0;j<entry.getValue();j++) {
                int boardPos = boardPosList.get(k++);
                putPiece(boardPos, (j%2 == 0) ? 1:0, piece);
            }
        }
    }

    private void randomPieces(int player) {
        final Set<Integer> generated = new LinkedHashSet<Integer>();
        while (generated.size() < StrategoConstants.MAX_PIECES) {
            Integer next;
            if (player == StrategoConstants.RED) {
                next = StrategoControl.randomBetween(StrategoConstants.RED_PLAYER[0], StrategoConstants.RED_PLAYER[1]);
            } else {
                next = StrategoControl.randomBetween(StrategoConstants.BLUE_PLAYER[0], StrategoConstants.BLUE_PLAYER[1]);
            }
            generated.add(next);
        }

        List<Integer> boardPosList = new ArrayList<Integer>(generated.size());
        boardPosList.addAll(generated);

        Map<PieceEnum, Integer> mapBoard = new HashMap<PieceEnum, Integer>();
        mapBoard.put(PieceEnum.MARSHALL, StrategoConstants.MAR_MAX);
        mapBoard.put(PieceEnum.GENERAL, StrategoConstants.GEN_MAX);
        mapBoard.put(PieceEnum.COLONEL, StrategoConstants.COR_MAX);
        mapBoard.put(PieceEnum.MAJOR, StrategoConstants.COM_MAX);
        mapBoard.put(PieceEnum.CAPITAN, StrategoConstants.CAP_MAX);
        mapBoard.put(PieceEnum.LIEUTENANT, StrategoConstants.TEN_MAX);
        mapBoard.put(PieceEnum.SERGEANT, StrategoConstants.SAR_MAX);
        mapBoard.put(PieceEnum.MINER, StrategoConstants.MIN_MAX);
        mapBoard.put(PieceEnum.SCOUT, StrategoConstants.EXP_MAX);
        mapBoard.put(PieceEnum.SPY, StrategoConstants.ESP_MAX);
        mapBoard.put(PieceEnum.BOMB, StrategoConstants.BOM_MAX);
        mapBoard.put(PieceEnum.FLAG, StrategoConstants.BAN_MAX);

        int k=0;
        for (Map.Entry<PieceEnum, Integer> entry : mapBoard.entrySet()) {
            PieceEnum piece = entry.getKey();
            for (int j=0;j<entry.getValue();j++) {
                int boardPos = boardPosList.get(k++);
                putPiece(boardPos, player, piece);
            }
        }
    }

    private void putPiece(int pos, int color, PieceEnum piece) {
        //Log.d("StrategoControl", "Adding piece:" + piece + " boardPos:" + pos + " player:" + color);
        pieces [pos] = new Piece();
        pieces [pos].setPlayer(color);
        pieces[pos].setPieceEnum(piece);
    }

    public Piece getPieceAt(int i) {
        return pieces[i];
    }

    public static boolean isPlayablePosition (final int pos) {
        return pos != StrategoConstants.c6 && pos != StrategoConstants.d6
                && pos != StrategoConstants.c5 && pos != StrategoConstants.d5
                && pos != StrategoConstants.g6 && pos != StrategoConstants.h6
                && pos != StrategoConstants.g5 && pos != StrategoConstants.h5;
    }

    public static int randomBetween (int low, int high) {
        return rng.nextInt(high-low) + low;
    }
}


