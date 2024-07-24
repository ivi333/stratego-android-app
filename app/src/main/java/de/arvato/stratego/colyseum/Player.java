package de.arvato.stratego.colyseum;

import io.colyseus.annotations.SchemaField;
import io.colyseus.serializer.schema.Schema;

public class Player extends Schema {

    @SchemaField(type = "0/string", ref = String.class)
    public String name = "";

    @SchemaField(type = "1/uint16", ref = Integer.class)
    public Integer color = -1;

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}
