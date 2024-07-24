package de.arvato.stratego.colyseum;

import android.os.Build;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.arvato.stratego.model.PlayerView;
import io.colyseus.Client;
import io.colyseus.Room;
import io.colyseus.serializer.schema.types.MapSchema;

import java.util.LinkedHashMap;
import java.util.Observable;

public class ColyseusManager extends Observable {

    private static final String TAG = "ColyseusManager";

    private static ColyseusManager instance;

    private Client client;

    private Room<GameState> room;

    private MutableLiveData<PlayerView> playerLiveData;
    private MutableLiveData<PlayerView> playerReadyLive;

    private ColyseusManager(String serverUrl) {
        client = new Client(serverUrl);
    }

    public static synchronized ColyseusManager getInstance(String serverUrl) {
        if (instance == null) {
            instance = new ColyseusManager(serverUrl);
        }
        return instance;
    }

    public MutableLiveData<PlayerView> getPlayerLiveData() {
        return playerLiveData;
    }

    public void setPlayerLiveData(MutableLiveData<PlayerView> playerLiveDta) {
        this.playerLiveData = playerLiveDta;
    }

    public MutableLiveData<PlayerView> getPlayerReadyLive() {
        return playerReadyLive;
    }

    public void setPlayerReadyLive(MutableLiveData<PlayerView> playerReadyLive) {
        this.playerReadyLive = playerReadyLive;
    }

    public void joinOrCreate (String name, int color) {

        LinkedHashMap<String, Object> options = new LinkedHashMap<>();
        options.put("name", name);
        options.put("color", color);

        client.joinOrCreate(GameState.class, "stratego_game", options,   r -> {
            Log.d(TAG, "Connected to room:" + r.getName());
            this.room = r;
            if (this.room.getId() != null) {
                Log.d(TAG, "Room ID:" + this.room.getId());
            }

            if (this.room.getSessionId() != null) {
                Log.d(TAG, "Session ID:" + room.getSessionId());
            }

            //state

            r.getState().players.setOnAdd((player, key) -> {
                Log.d(TAG, "Player added: " + key + " -> " + player);
                if (!key.equals(this.room.getSessionId())) {
                    Log.d(TAG, "A new player has been connected");
                    playerLiveData.postValue(new PlayerView(player.name, player.color));
                } else {
                    Log.d(TAG, "Me added as a player");
                }
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

            r.onMessage("ready", Player.class, (Player player) -> {
                Log.d(TAG, "Client is ready:" + player);
                playerReadyLive.postValue(new PlayerView(player.name, player.color));
            });

            //r.getState().players.triggerAll();

        }, Throwable::printStackTrace);
    }

    public String getRoomID () {
        if (room != null && room.getId() != null)
            return this.room.getId();

        return "";
    }

    /*public void sendInitialPlayer (String name, Integer color) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.createObjectNode()
                .put("name", name)
                .put("color", color);
        room.send("initial_data", jsonNode);
    }*/

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

