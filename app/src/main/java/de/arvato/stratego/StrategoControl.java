package de.arvato.stratego;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class StrategoControl {

    private static final Random rng = new Random();


    protected Piece [] pieces;
    protected boolean myTurn;
    protected int myColor;

    public StrategoControl () {
        myTurn=false;
        myColor = StrategoConstants.RED;
        pieces = new Piece [100];

        randomPieces (StrategoConstants.RED);
        randomPieces (StrategoConstants.BLUE);

        /*putPiece (0, StrategoConstants.RED, StrategoConstants.FLAG);
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
        putPiece (11, StrategoConstants.RED, StrategoConstants.BOMB);*/

        /*for (int z = 12; z<50; z++) {
            putPiece(z, StrategoConstants.RED, StrategoConstants.MINER);
        }

        for (int z = 50; z<100; z++) {
            putPiece(z, StrategoConstants.BLUE, StrategoConstants.BOMB);
        }*/

    }

    private void randomPieces(int player) {
        Set<Integer> generated = new LinkedHashSet<Integer>();
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
        Log.d("StrategoControl", "Adding piece:" + piece + " boardPos:" + pos + " player:" + color);
        pieces [pos] = new Piece();
        pieces [pos].setColor(color);
        pieces [pos].setId(piece.getPosition());
        pieces [pos].setPoints(piece.getPoints());
    }

    public Piece getPieceAt(int i) {
        return pieces[i];
    }

    public static boolean isFieldBoardWithBorder (final int pos) {
        return pos != StrategoConstants.c6 && pos != StrategoConstants.d6
                && pos != StrategoConstants.c5 && pos != StrategoConstants.d5
                && pos != StrategoConstants.g6 && pos != StrategoConstants.h6
                && pos != StrategoConstants.g5 && pos != StrategoConstants.h5;

    }

    public static int randomBetween (int low, int high) {
        return rng.nextInt(high-low) + low;
    }
}


