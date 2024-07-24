package de.arvato.stratego.colyseum;

import io.colyseus.annotations.SchemaField;
import io.colyseus.serializer.schema.Schema;

public class PrimitivesTest extends Schema {
    @SchemaField(type = "0/uint16")
    public Integer turn;
}
