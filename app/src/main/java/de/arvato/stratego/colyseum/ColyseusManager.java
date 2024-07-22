package de.arvato.stratego.colyseum;

import android.os.Build;
import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;

import io.colyseus.Client;
import io.colyseus.Room;
import io.colyseus.serializer.schema.DataChange;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ColyseusManager {

    private static final String TAG = "ColyseusManager";

    private static ColyseusManager instance;

    private Client client;

    private Room<GameState> room;

    private ColyseusManager(String serverUrl) {
        client = new Client(serverUrl);
    }

    public static synchronized ColyseusManager getInstance(String serverUrl) {
        if (instance == null) {
            instance = new ColyseusManager(serverUrl);
        }
        return instance;
    }

    public void joinOrCreate () {

        client.joinOrCreate(GameState.class, "stratego_game", r -> {
            Log.d(TAG, "Connected to room:" + r.getName());
            this.room = r;
            Log.d(TAG, this.room.toString());

            //state

            r.getState().players.setOnAdd((player, key) -> {
                Log.d(TAG, "Player added: " + key + " -> " + player);
            });

            r.getState().players.setOnRemove((player, key) -> {
                Log.d(TAG, "Player removed: " + key);
            });

            r.getState().players.setOnChange((player, key) -> {
                Log.d(TAG, "Player changed: " + key + " -> " + player);
            });

            r.getState().primitives.setOnChange(changes -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    changes.forEach(change -> {
                        Log.d(TAG, change.getField() + ": " + change.getPreviousValue() + " -> " + change.getValue());
                    });
                }
            });

            r.setOnStateChange((state, isFirstState) -> {
                Log.d(TAG, "onStateChange()");
                Log.d(TAG, "state.primitives.turn = " + state.primitives.turn);
                Log.d(TAG, "isFirstState:" + isFirstState);
            });


            //messages listener
            r.onMessage("move", JsonNode.class, (JsonNode message) -> {
                String from = message.get("from").asText();
                String to = message.get("to").asText();
                Log.d(TAG, "move:" + "From:" + from + " to:" + to);
            });

            r.onMessage("client_left", JsonNode.class, (JsonNode message) -> {
                String sessionId = message.get("sessionId").asText();
                Log.d(TAG, "Client:" + sessionId + " left the room");
            });

            //r.getState().players.triggerAll();

        }, Throwable::printStackTrace);
    }

    public String getRoomID () {
        if (room != null && room.getId() != null)
            return this.room.getId();

        return "";
    }

    public void disconnect () {
        this.room.leave();
    }

    public void sendFakeMove () throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.createObjectNode()
                .put("from", "32")
                .put("to", "34");
        room.send("fakeMove", jsonNode);
    }

}

