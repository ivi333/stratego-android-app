package de.arvato.stratego.colyseum;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.arvato.stratego.model.PlayerView;
import io.colyseus.Client;
import io.colyseus.Room;

import java.util.Observable;

public class ColyseusManager extends Observable {

    private static final String TAG = "ColyseusManager";

    private static ColyseusManager instance;

    private Client client;

    private Room<GameState> room;

    private MutableLiveData<PlayerView> playerLiveData;

    //private final Handler mainHandler = new Handler(Looper.getMainLooper());

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

    public void setPlayerLiveDta(MutableLiveData<PlayerView> playerLiveDta) {
        this.playerLiveData = playerLiveDta;
    }

    public void joinOrCreate () {

        client.joinOrCreate(GameState.class, "stratego_game", r -> {
            Log.d(TAG, "Connected to room:" + r.getName());
            this.room = r;
            Log.d(TAG, this.room.toString());

            //state

            r.getState().players.setOnAdd((player, key) -> {
                Log.d(TAG, "Player added: " + key + " -> " + player);
                //mainHandler.post(() -> playerLiveData.setValue(player));
                //playerLiveDta.postValue();
                playerLiveData.postValue(new PlayerView(player.name, player.color));
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

