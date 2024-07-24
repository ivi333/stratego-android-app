package de.arvato.stratego.model;

public class PlayerView {

    private String name;
    private Integer color;


    public PlayerView(String name, Integer color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "PlayerView{" +
                "name='" + name + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}
