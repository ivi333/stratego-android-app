package de.arvato.stratego.colyseum;

import io.colyseus.annotations.SchemaField;
import io.colyseus.serializer.schema.Schema;

public class Player extends Schema {

    @SchemaField(type = "0/string")
    public String name = "";

    @SchemaField(type = "1/string")
    public String color = "";

}
