package de.arvato.stratego.colyseum;

import io.colyseus.annotations.SchemaField;
import io.colyseus.serializer.schema.Schema;
import io.colyseus.serializer.schema.types.ArraySchema;
import io.colyseus.serializer.schema.types.MapSchema;

public class GameState extends Schema {
    @SchemaField(type = "0/map/ref", ref = Player.class)
    public MapSchema<Player> players = new MapSchema<>(Player.class);

    @SchemaField(type = "1/ref", ref = PrimitivesTest.class)
    public PrimitivesTest primitives = new PrimitivesTest();

    @SchemaField(type = "2/map/ref", ref = PieceArray.class)
    public MapSchema<PieceArray> mapPieces = new MapSchema<>(PieceArray.class);

    /*@SchemaField(type = "2/array/ref", ref = PieceArray.class)
    public ArraySchema<PieceArray> pieces = new ArraySchema<>(PieceArray.class);*/

}
