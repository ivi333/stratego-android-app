package de.arvato.stratego.colyseum;

import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;

import org.json.JSONObject;

import io.colyseus.Client;
import io.colyseus.Room;

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


            r.onMessage("move", JsonNode.class, (JsonNode message) -> {
                String from = message.get("from").asText();
                String to = message.get("to").asText();
                Log.d(TAG, "move:" + "From:" + from + " to:" + to);
            });

        }, Throwable::printStackTrace);
    }

    public String getRoomID () {
        return this.room.getId();
    }

    public void disconnect () {
        this.room.leave();
    }

}

