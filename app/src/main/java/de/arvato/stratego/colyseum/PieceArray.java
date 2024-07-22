package de.arvato.stratego.colyseum;

import io.colyseus.annotations.SchemaField;
import io.colyseus.serializer.schema.Schema;
import io.colyseus.serializer.schema.types.ArraySchema;

public class PieceArray extends Schema {
    @SchemaField(type = "0/array/ref", ref = String.class)
    public ArraySchema<String> pieces = new ArraySchema<>(String.class);

}
