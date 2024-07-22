package de.arvato.stratego.colyseum;

import io.colyseus.annotations.SchemaField;
import io.colyseus.serializer.schema.Schema;
import io.colyseus.serializer.schema.types.ArraySchema;

public class ChatState extends Schema {
    @SchemaField(type = "0/string")
    public String message = "";
}
