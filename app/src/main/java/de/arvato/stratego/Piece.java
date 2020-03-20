package de.arvato.stratego;

public class Piece {
    private int color;
    private int id;
    private int points;

    public void setColor(int color) {
        this.color = color;
    }

    public void setId(int name) {
        this.id = name;
    }

    public int getColor() {
        return color;
    }

    public int getId() {
        return id;
    }
    public void setPoints(int points) {
        this.points = points;
    }

    public int getPoints() {
        return points;
    }
}