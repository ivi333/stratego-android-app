package de.arvato.stratego.colyseum;

import io.colyseus.annotations.SchemaField;
import io.colyseus.serializer.schema.Schema;

public class Message extends Schema {
    @SchemaField(type = "0/string")
    public String message = "";

}
