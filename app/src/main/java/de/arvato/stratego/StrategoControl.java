package de.arvato.stratego;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import de.arvato.stratego.game.Board;
import de.arvato.stratego.game.Piece;
import de.arvato.stratego.game.PieceEnum;


public class StrategoControl extends Observable  {

    public static final String TAG = "StrategoControl";
    public static final boolean fakeAllPieces = true;
    public static final boolean fakeGame = false;
    private static final Random rng = new Random();
    protected int selectPos;
    protected long lClockStartRed, lClockStartBlue, lClockRed, lClockBlue;
    protected long lClockTotal = 600000;
    protected Board board;

    public StrategoControl (int player) {
        board = new Board();
        board.changeGameStatus(StrategoConstants.GameStatus.INIT_BOARD);
        board.initTurn(player);
        board.humanPlayer(player);
        selectPos=-1;
        if (fakeGame) {
            startFakeGame();
        }
    }

    public boolean startGame () {
        boolean canStart=true;
        int y, z;
        if (board.getHumanPlayer() == StrategoConstants.RED) {
            y = StrategoConstants.RED_PLAYER[0];
            z = StrategoConstants.RED_PLAYER[1];
        } else {
            y = StrategoConstants.BLUE_PLAYER[0];
            z = StrategoConstants.BLUE_PLAYER[1];
        }
        for (int x=y ; x < z && canStart; x++ ) {
            if (board.getPieceAt(x) == null) {
                canStart=false;
            }
        }
        if (canStart) {
            board.initPossibleBombs (y, z);
            board.changeGameStatus(StrategoConstants.GameStatus.PLAY);
            continueTimer();
        }
        return canStart;
    }

    public void startFakeGame () {
        board.initTurn(StrategoConstants.RED);
        board.humanPlayer(StrategoConstants.RED);
        if (fakeAllPieces) {
            randomPieces(StrategoConstants.RED);
            randomPieces(StrategoConstants.BLUE);
        } else {
            randomFake ();
        }
        board.changeGameStatus(StrategoConstants.GameStatus.PLAY);
    }

    public void movePiece (int to) {
//        System.out.println("movePiece from:" + selectPos + " to:" + to + " gameStatus:" + board.getGameStatus().name());
        switch (board.getGameStatus()) {
            case PLAY:
                board.move (to, selectPos);
                changeTurn();
                selectPos=-1;
                break;
            case INIT_BOARD:
                board.initBoard (to, selectPos);
                selectPos=-1;
                break;
            case DRAW_REPEAT:
                //TODO
                break;
            default:
                break;
        }
    }


    private void changeTurn() {
        switchTimer();
        board.changeTurn();
        if (board.getTurn() == board.getAIPlayer()) {
            //playAI ();
        }
    }

    /*public void playAI () {
        Thread searchThread = new Thread(new StrategoAI(getBoard(), this));
        searchThread.start();
    }*/

    public boolean selectPiece (int pos) {
        switch(board.getGameStatus()) {
            case INIT_BOARD:
                if (selectPos == pos) {
                    return true;
                } else if (selectPos != -1) {
                    return false;
                } else {
                    Piece piece = board.getPieceAt(pos);
                    if (piece != null && piece.getPlayer() == board.getHumanPlayer()) {
                        selectPos = pos;
                        return true;
                    }
                    return false;
                }
            case PLAY:
                Piece piece = board.getPieceAt(pos);
                if (piece != null && piece.getPlayer() == board.getTurn()) {
                    selectPos = pos;
                    return true;
                }
                return false;
            default:
                break;
        }
        return false;
    }

    private void randomFake () {
        final Set<Integer> generated = new LinkedHashSet<Integer>();
        Integer next;
        while (generated.size() < StrategoConstants.MAX_PIECES) {
            next = StrategoControl.randomBetween(0, StrategoConstants.BOARD_SIZE);
            if (Board.isPlayablePosition(next))
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
        //System.out.println("StrategoControl", "Adding piece:" + piece + " boardPos:" + pos + " player:" + color);
        Piece tmpPiece = new Piece();
        tmpPiece.setPlayer(color);
        tmpPiece.setPieceEnum(piece);
        board.setPieceAt(tmpPiece, pos);
    }

    protected void switchTimer(){
        final long lEnd = System.currentTimeMillis();
        if(lClockStartRed > 0 && board.getTurn() == StrategoConstants.RED){
            lClockRed += (lEnd - lClockStartRed);
            lClockStartRed = 0;
            lClockStartBlue = lEnd;
        } else if(lClockStartBlue > 0 && board.getTurn() == StrategoConstants.BLUE){
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
            if(board.getTurn() == StrategoConstants.RED)
                lClockStartRed = System.currentTimeMillis();
            else
                lClockStartBlue = System.currentTimeMillis();
        }
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
//            System.out.println("manage piece:" + entry.getKey().getName());
            for (int j=0;j<entry.getValue();j++) {
                startPieces[k++] = entry.getKey();
            }
        }
        return startPieces;
    }

    protected long getBlueRemainClock(){
        final long lDiff = lClockStartBlue > 0 ? (System.currentTimeMillis() - lClockStartBlue) : 0;
        return (lClockTotal - (lClockBlue + lDiff));
    }

    protected long getRedRemainClock(){
        final long lDiff = lClockStartRed > 0 ? (System.currentTimeMillis() - lClockStartRed) : 0;
        return (lClockTotal - (lClockRed + lDiff));
    }

    public Board getBoard() {
        return board;
    }

    public static boolean isPlayablePosition (final int pos) {
        return pos != StrategoConstants.c6 && pos != StrategoConstants.d6
                && pos != StrategoConstants.c5 && pos != StrategoConstants.d5
                && pos != StrategoConstants.g6 && pos != StrategoConstants.h6
                && pos != StrategoConstants.g5 && pos != StrategoConstants.h5;
    }

    /*public static void main (String args[]) throws Exception{
        StrategoControl control = new StrategoControl(StrategoConstants.RED);
        control.randomPieces(StrategoConstants.RED);
        control.randomPieces(StrategoConstants.BLUE);
        control.board.printPieces();
        Scanner sc = new Scanner(System.in);
        while (true) {
            int from = sc.nextInt();
            int to = sc.nextInt();
            System.out.println(from + " ->" + to);
            Thread searchThread = new Thread(new StrategoAI(control.getBoard(), control));
            searchThread.start();
        }
    }*/

    /*@Override
    public void moveAI(int from, int to) {
        System.out.println("AI is moving:" + from + "->" + to);
        if (from != -1 && to != -1) {
            selectPiece(from);
            movePiece(to);
            setChanged();
            notifyObservers();
        } else {
            //TODO Throw Error
            Log.e(TAG, "Wrong position got from AI!");
        }
    }*/
}
