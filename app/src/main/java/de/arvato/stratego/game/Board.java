package de.arvato.stratego.game;

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
    protected int humanPlayer;
    protected int turn;
    protected Map<PieceEnum, Integer> capturedPiecesRed;
    protected Map<PieceEnum, Integer> capturedPiecesBlue;
    protected StrategoConstants.GameStatus gameStatus;
    protected int countMoves;
    protected List<Integer> possibleBombsHuman;
    protected List<HistoryPiece> historyMoves;


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

    public void humanPlayer (final int player) {
        this.humanPlayer = player;
    }

    public int getHumanPlayer () {
        return this.humanPlayer;
    }

    public int getAIPlayer () {
        return this.humanPlayer == StrategoConstants.RED ? StrategoConstants.BLUE : StrategoConstants.RED;
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

    public void initBoard (int to, int selectPos) {
        int x1, x2;
        if (humanPlayer == StrategoConstants.RED) {
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

    public void move (int to, int selectPos) {
        if (selectPos != -1 && isPlayablePosition(to)) {
            if (!isValidMovement (selectPos, to)) {
                System.out.println("Move no possible from:" + selectPos + " to:" + to);
                return;
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

            if (fightStatus != PieceFightStatus.NO_FIGHT) {
                //TODO
            }
            boolean updatedBomb = updatePossibleBoms (selectPos);
            addHistory (selectPos, to, pieceFrom, pieceTo, fightStatus, updatedBomb, getTurn());
            countMoves++;
            selectPos = -1;
        } else {
            System.out.println("Move no possible from:" + selectPos + " to:" + to);
        }
    }

    private void addHistory(int selectPos, int to, Piece pieceFrom, Piece pieceTo, PieceFightStatus fightStatus, boolean updatedBomb, int player) {
        HistoryPiece history = new HistoryPiece();
        history.moveFrom = selectPos;
        history.moveTo = to;
        history.pieceFrom = pieceFrom;
        history.pieceTo = pieceTo;
        history.fightStatus = fightStatus;
        history.possibleBomb = updatedBomb;
        history.player = player;
        historyMoves.add(history);
    }

    public void undoLastMove () {
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
            if (history.possibleBomb) {
                possibleBombsHuman.add(history.moveFrom);
            }
            countMoves--;
        }
    }

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

    private boolean updatePossibleBoms(int selectPos) {
        if (turn == getHumanPlayer()) {
            Integer i = Integer.valueOf(selectPos);
            if (possibleBombsHuman.contains(i)) {
                possibleBombsHuman.remove(i);
                return true;
            }
        }
        return false;
    }

    /*public Board cloneBoard () {
        Board newBoard = new Board();
        newBoard.capturedPiecesBlue = CopyUtil.clone(this.capturedPiecesBlue);
        newBoard.capturedPiecesRed = CopyUtil.clone(this.capturedPiecesRed);
        newBoard.gameStatus = this.gameStatus;
        newBoard.humanPlayer = this.humanPlayer;
        newBoard.pieces = CopyUtil.clonePieces(this.pieces);
        newBoard.turn = this.turn;
        newBoard.countMoves = this.countMoves;
        newBoard.possibleBombsHuman = CopyUtil.clone(this.possibleBombsHuman);
        newBoard.historyMoves = CopyUtil.clone(this.historyMoves);
        return newBoard;
    }*/

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

    public Map<Integer, List<Integer>> getPiecesWithMovement () {
        Map<Integer, List<Integer>> mapPieces = new HashMap<Integer, List<Integer>>();
        for (int z=0;z<StrategoConstants.BOARD_SIZE;z++) {
            if (getPieceAt(z) != null && getPieceAt(z).getPlayer() == this.turn) {
                List<Integer> futureMoves = getPossibleMovements(z);
                if (!futureMoves.isEmpty()) {
                    mapPieces.put(z, futureMoves);
                }
            }
        }
        return mapPieces;
    }

    public Map<Integer, List<Integer>> getPiecesDiscoveredWithMovement (int player) {
        Map<Integer, List<Integer>> mapPieces = new HashMap<Integer, List<Integer>>();
        for (int z=0;z<StrategoConstants.BOARD_SIZE;z++) {
            if (getPieceAt(z) != null && getPieceAt(z).getPlayer() == player && getPieceAt(z).isPieceDiscovered()) {
                List<Integer> futureMoves = getPossibleMovements(z);
                if (!futureMoves.isEmpty()) {
                    mapPieces.put(z, futureMoves);
                }
            }
        }
        return mapPieces;
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
        System.out.println(sb.toString());
    }

    public StrategoConstants.GameStatus getGameStatus() {
        return gameStatus;
    }

    public boolean hasPiecesDiscovered () {
        return this.hasDiscoveredPieces(getHumanPlayer());
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

    public int piecesDiscoveredCount (int player) {
        int total=0;
        for (int z=0;z<StrategoConstants.BOARD_SIZE;z++) {
            if (pieces[z]!=null && pieces[z].getPlayer() == player && pieces[z].isPieceDiscovered()) {
                total++;
            }
        }
        return total;
    }

    public boolean hasDiscoveredPiece (int player, PieceEnum piece) {
        boolean res=false;
        for (int z=0;z<StrategoConstants.BOARD_SIZE && !res;z++) {
            if (pieces[z]!=null && pieces[z].getPlayer() == player && pieces[z].getPieceEnum() == piece && pieces[z].isPieceDiscovered()) {
                res=true;
            }
        }
        return res;
    }

    /**
     * @param player
     * @param discovered
     * @return
     */
    public Map<PieceEnum, List<Integer>> getPositionsPiecesByDiscovered (int player, boolean discovered) {
        Map<PieceEnum, List<Integer>> result = new HashMap<>();
        for (int z=0;z<StrategoConstants.BOARD_SIZE;z++) {
            if (pieces[z]!=null && pieces[z].getPlayer() == player && pieces[z].isPieceDiscovered() == discovered) {
                Piece p = pieces[z];
                if (!result.containsKey(p.getPieceEnum())) {
                    result.put(p.getPieceEnum(), new ArrayList<Integer>());
                }
                result.get(p.getPieceEnum()).add(z);
            }
        }
        return result;
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

    public Map<PieceEnum, Integer> getCapturedPiecesHumanPlayer () {
        if (humanPlayer == StrategoConstants.RED) {
            return this.getCapturedPiecesRed();
        } else {
            return this.getCapturedPiecesBlue();
        }
    }

    public Map<PieceEnum, Integer> getCapturedPiecesAI () {
        if (getAIPlayer() == StrategoConstants.RED) {
            return this.getCapturedPiecesRed();
        } else {
            return this.getCapturedPiecesBlue();
        }
    }

    public int getCountMoves() {
        return countMoves;
    }

    public void initPossibleBombs(int y, int z) {
        for (int x=y ; x < z; x++ ) {
            possibleBombsHuman.add(x);
        }
    }

    public int getPointsCapturedPieces(int player) {
        Map<PieceEnum, Integer> tmp;
        if (player == getHumanPlayer()) {
            tmp = this.getCapturedPiecesHumanPlayer();
        } else {
            tmp = this.getCapturedPiecesAI();
        }
        int total=0;
        for (Map.Entry<PieceEnum, Integer> entry : tmp.entrySet()) {
            int points = entry.getKey().getPoints();
            int count = entry.getValue();
            total += points * count;
        }
        return total;
    }

    public int getAmountPiecesCaptured (int player) {
        Map<PieceEnum, Integer> tmp;
        if (player == getHumanPlayer()) {
            tmp = this.getCapturedPiecesHumanPlayer();
        } else {
            tmp = this.getCapturedPiecesAI();
        }
        int total=0;
        for (Map.Entry<PieceEnum, Integer> entry : tmp.entrySet()) {
            total+=entry.getValue();
        }
        return total;
    }

    public List<Integer> getPossibleBombsHuman() {
        return possibleBombsHuman;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Board [pieces=");
        builder.append(Arrays.toString(pieces));
        builder.append(", humanPlayer=");
        builder.append(humanPlayer);
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
        builder.append("humanPlayer=");
        builder.append(humanPlayer);
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
        System.out.println(builder.toString());
    }

    public void printDiscoveredPieces () {
        for (int z=0;z<StrategoConstants.BOARD_SIZE ;z++) {
            if (pieces[z]!=null && pieces[z].isPieceDiscovered()) {
                System.out.println("Disc.Piece Pos:" + z + " " + pieces[z]);
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
}