package de.arvato.stratego.colyseum;

import io.colyseus.annotations.SchemaField;
import io.colyseus.serializer.schema.Schema;
import io.colyseus.serializer.schema.types.MapSchema;

public class GameState extends Schema {
    @SchemaField(type = "0/map/ref", ref = PieceArray.class)
    public MapSchema<PieceArray> pieceMap = new MapSchema<>(PieceArray.class);

    @SchemaField(type = "1/int16")
    public Short turn = 0;

    @SchemaField(type = "2/map/ref", ref = Player.class)
    public MapSchema<Player> players = new MapSchema<>(Player.class);

}
