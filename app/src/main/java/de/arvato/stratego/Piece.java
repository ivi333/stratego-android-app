package de.arvato.stratego;

public class Piece {
    private int player;
    //private int id;
    //private int points;
    //private int player;
    private PieceEnum pieceEnum;

    /*public void setColor(int color) {
        this.color = color;
    }*/

    /*public void setId(int name) {
        this.id = name;
    }*/

    public PieceEnum getPieceEnum() {
        return pieceEnum;
    }

    public void setPieceEnum(PieceEnum pieceEnum) {
        this.pieceEnum = pieceEnum;
    }
    /*public int getColor() {
        return color;
    }*/
    public int getPlayer() {
        return player;
    }

    public void setPlayer(int player) {
        this.player = player;
    }
    /*
    public int getId() {
        return id;
    }
    public void setPoints(int points) {
        this.points = points;
    }
    public int getPoints() {
        return points;
    }*/

}