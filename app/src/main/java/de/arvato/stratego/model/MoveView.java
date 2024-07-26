package de.arvato.stratego.model;

import de.arvato.stratego.StrategoConstants;

public class MoveView {
    private Integer from;
    private Integer to;

    public MoveView(Integer from, Integer to) {
        this.from = from;
        this.to = to;
    }

    public MoveView transformMoveToEnemyBoard () {
        return new MoveView(
                convertIndexToOpposite(from),
                convertIndexToOpposite(to)
        );
    }

    public static int convertIndexToOpposite (int idx) {
        return (100 - idx) - 1;
    }

    public Integer getFrom() {
        return from;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }

    public Integer getTo() {
        return to;
    }

    public void setTo(Integer to) {
        this.to = to;
    }

    @Override
    public String toString() {
        return "MoveView{" +
                "from=" + from +
                ", to=" + to +
                '}';
    }
}
