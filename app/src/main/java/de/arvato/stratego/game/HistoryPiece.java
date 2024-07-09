package de.arvato.stratego.game;

public class HistoryPiece {

    public int moveFrom;
    public int moveTo;
    public PieceFightStatus fightStatus;;
    public Piece pieceFrom;
    public Piece pieceTo;
    public boolean possibleBomb;
    public int player;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("HistoryPiece [moveFrom=");
        builder.append(moveFrom);
        builder.append(", moveTo=");
        builder.append(moveTo);
        builder.append(", fightStatus=");
        builder.append(fightStatus);
        builder.append(", pieceFrom=");
        builder.append(pieceFrom);
        builder.append(", pieceTo=");
        builder.append(pieceTo);
        builder.append(", possibleBomb=");
        builder.append(possibleBomb);
        builder.append(", player=");
        builder.append(player);
        builder.append("]");
        return builder.toString();
    }
}