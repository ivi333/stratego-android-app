package de.arvato.stratego;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import de.arvato.stratego.util.Pos;

public class StrategoControl {

    public static final String TAG = "StrategoControl";

    private static final Random rng = new Random();

    public static final boolean fakeAllPieces = false;
    public static final boolean fakeGame = true;


    protected Piece [] pieces;
    protected int myPlayer;
    protected int turn;
    protected int selectPos;
    protected Map<PieceEnum, Integer> capturedPiecesRed;
    protected Map<PieceEnum, Integer> capturedPiecesBlue;
    protected StrategoConstants.GameStatus gameStatus;
    protected long lClockStartRed, lClockStartBlue, lClockRed, lClockBlue;
    protected long lClockTotal = 600000;


    public StrategoControl (int player) {
        pieces = new Piece [100];
        capturedPiecesBlue = new HashMap<PieceEnum, Integer>();
        capturedPiecesRed = new HashMap<PieceEnum, Integer>();
        gameStatus = StrategoConstants.GameStatus.INIT_BOARD;
        //Define who start the game
        turn = StrategoConstants.RED;
        //Define my player color
        myPlayer = player;
        selectPos=-1;
        if (fakeGame) {
            startFakeGame();
        }
    }

    public boolean startGame () {
        boolean canStart=true;
        int y, z;
        if (myPlayer == StrategoConstants.RED) {
            y = StrategoConstants.RED_PLAYER[0];
            z = StrategoConstants.RED_PLAYER[1];
        } else {
            y = StrategoConstants.BLUE_PLAYER[0];
            z = StrategoConstants.BLUE_PLAYER[1];
        }
        for (int x=y ; x < z && canStart; x++ ) {
            if (pieces[x] == null) {
                canStart=false;
            }
        }
        if (canStart) {
            gameStatus = StrategoConstants.GameStatus.PLAY;
            continueTimer();
        }
        return canStart;
    }

    public void startFakeGame () {
        turn = StrategoConstants.RED;
        myPlayer = StrategoConstants.RED;
        if (fakeAllPieces) {
            randomPieces(StrategoConstants.RED);
            randomPieces(StrategoConstants.BLUE);
        } else {
            randomFake ();
        }
        gameStatus= StrategoConstants.GameStatus.PLAY;
    }

    public void movePiece (int to) {
        Log.d(TAG, "movePiece to:" + to + " gameStatus:" + gameStatus.name());
        switch (gameStatus) {
            case PLAY:
                play (to);
                break;
            case INIT_BOARD:
                initBoard (to);
                break;
            case DRAW_REPEAT:
                //TODO
                break;
        }
    }

    private void play (int to) {
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
                    capturePiece(pieceFrom.getPlayer(), pieceFrom.getPieceEnum());
                    capturePiece(pieceTo.getPlayer(), pieceTo.getPieceEnum());
                } else if (pieceTo.getPieceEnum() == PieceEnum.FLAG) {
                    pieces[to] = pieces[selectPos];
                    pieces[selectPos] = null;
                    capturePiece(pieceTo.getPlayer(), pieceTo.getPieceEnum());
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
                        capturePiece(pieceTo.getPlayer(), pieceTo.getPieceEnum());
                    } else {
                        //fight one vs one
                        if (pieceFrom.getPieceEnum().getPoints() > pieceTo.getPieceEnum().getPoints()) {
                            pieces[to] = pieces[selectPos];
                            pieces[selectPos] = null;
                            capturePiece(pieceTo.getPlayer(), pieceTo.getPieceEnum());
                        } else {
                            pieces[selectPos] = null;
                            capturePiece(pieceFrom.getPlayer(), pieceFrom.getPieceEnum());
                        }
                    }
                }
            }
            selectPos = -1;
            changeTurn ();
        }
    }

    private void initBoard (int to) {
        int x1, x2;
        if (myPlayer == StrategoConstants.RED) {
            x1 = StrategoConstants.RED_PLAYER[0];
            x2 = StrategoConstants.RED_PLAYER[1];
        } else {
            x1 = StrategoConstants.BLUE_PLAYER[0];
            x2 = StrategoConstants.BLUE_PLAYER[1];
        }
        if (selectPos != -1 && to >= x1 && to < x2 ) {
            //interchange pos
            Piece tmp = pieces[to];
            pieces[to] = pieces[selectPos];
            pieces[selectPos] = tmp;
        }
        selectPos = -1;
    }

    public void finishGame() {
        //TODO
    }

    private void changeTurn() {
        switchTimer();
        if (turn == StrategoConstants.RED) {
            turn = StrategoConstants.BLUE;
        } else {
            turn = StrategoConstants.RED;
        }
    }

    public boolean selectPiece (int pos) {
        switch(gameStatus) {
            case INIT_BOARD:
                if (selectPos == pos) {
                    return true;
                } else if (selectPos != -1) {
                    return false;
                } else {
                    Piece piece = pieces[pos];
                    if (piece != null && piece.getPlayer() == myPlayer) {
                        selectPos = pos;
                        return true;
                    }
                    return false;
                }
            case PLAY:
                Piece piece = pieces[pos];
                if (piece != null && piece.getPlayer() == turn) {
                    selectPos = pos;
                    return true;
                }
                return false;
        }
        return false;
    }

    public boolean isValidMovement (int selectedPos, int pos) {
        //int [] possibilities = getPossibleMovements(selectedPos);
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
                if (nextPos >= 0 && nextPos < 100) {
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
                if (nextPos >= 0 && nextPos < 100) {
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
                if (nextPos >= 0 && nextPos < 100) {
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
                if (nextPos >= 0 && nextPos < 100) {
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
        mapBoard.put(PieceEnum.CAPTAIN, StrategoConstants.CAP_MAX);
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

    public void randomPieces(int player) {
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
        mapBoard.put(PieceEnum.CAPTAIN, StrategoConstants.CAP_MAX);
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

    public void putPiece(int pos, int color, PieceEnum piece) {
        //Log.d("StrategoControl", "Adding piece:" + piece + " boardPos:" + pos + " player:" + color);
        pieces [pos] = new Piece();
        pieces [pos].setPlayer(color);
        pieces [pos].setPieceEnum(piece);
    }

    public Piece getPieceAt(int i) {
        return pieces[i];
    }

    public void capturePiece (final int player, PieceEnum piece) {
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

    protected void switchTimer(){
        final long lEnd = System.currentTimeMillis();
        if(lClockStartRed > 0 && turn == StrategoConstants.RED){
            lClockRed += (lEnd - lClockStartRed);
            lClockStartRed = 0;
            lClockStartBlue = lEnd;
        } else if(lClockStartBlue > 0 && turn == StrategoConstants.BLUE){
            lClockBlue += (lEnd - lClockStartBlue);
            lClockStartBlue = 0;
            lClockStartRed = lEnd;
        }
    }

    public void resetTimer(){
        lClockRed = 0;
        lClockBlue = 0;
        lClockStartRed = 0;
        lClockStartBlue = 0;
        continueTimer();
    }

    //TODO Use after pausing
    protected void continueTimer(){
        if(lClockTotal > 0){
            if(turn == StrategoConstants.RED)
                lClockStartRed = System.currentTimeMillis();
            else
                lClockStartBlue = System.currentTimeMillis();
        }
    }

    protected long getBlueRemainClock(){
        final long lDiff = lClockStartBlue > 0 ? (System.currentTimeMillis() - lClockStartBlue) : 0;
        return (lClockTotal - (lClockBlue + lDiff));
    }

    protected long getRedRemainClock(){
        final long lDiff = lClockStartRed > 0 ? (System.currentTimeMillis() - lClockStartRed) : 0;
        return (lClockTotal - (lClockRed + lDiff));
    }

    public Map<PieceEnum, Integer> getCapturedPiecesRed() {
        return capturedPiecesRed;
    }

    public Map<PieceEnum, Integer> getCapturedPiecesBlue() {
        return capturedPiecesBlue;
    }

    public StrategoConstants.GameStatus getGameStatus() {
        return gameStatus;
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

    public static PieceEnum [] createStartPiece (int player) {
        PieceEnum startPieces [] = new PieceEnum [40];
        Map<PieceEnum, Integer> tmpPieces = new TreeMap<>();
        tmpPieces.put(PieceEnum.MARSHALL, StrategoConstants.MAR_MAX);
        tmpPieces.put(PieceEnum.GENERAL, StrategoConstants.GEN_MAX);
        tmpPieces.put(PieceEnum.COLONEL, StrategoConstants.COR_MAX);
        tmpPieces.put(PieceEnum.MAJOR, StrategoConstants.COM_MAX);
        tmpPieces.put(PieceEnum.CAPTAIN, StrategoConstants.CAP_MAX);
        tmpPieces.put(PieceEnum.LIEUTENANT, StrategoConstants.TEN_MAX);
        tmpPieces.put(PieceEnum.SERGEANT, StrategoConstants.SAR_MAX);
        tmpPieces.put(PieceEnum.MINER, StrategoConstants.MIN_MAX);
        tmpPieces.put(PieceEnum.SCOUT, StrategoConstants.EXP_MAX);
        tmpPieces.put(PieceEnum.SPY, StrategoConstants.ESP_MAX);
        tmpPieces.put(PieceEnum.FLAG, StrategoConstants.BAN_MAX);
        tmpPieces.put(PieceEnum.BOMB, StrategoConstants.BOM_MAX);
        int k=0;
        for (Map.Entry<PieceEnum, Integer> entry : tmpPieces.entrySet()) {
            Log.d(TAG, "manage piece:" + entry.getKey().getName());
            for (int j=0;j<entry.getValue();j++) {
                startPieces[k++] = entry.getKey();
            }
        }
        return startPieces;
    }
}
