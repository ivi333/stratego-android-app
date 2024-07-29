package de.arvato.stratego.colyseum;

import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import de.arvato.stratego.colyseum.interfaces.JoinRoomCallback;
import de.arvato.stratego.exceptions.GenericColyseusException;
import de.arvato.stratego.exceptions.RoomNotFoundException;
import de.arvato.stratego.game.Piece;
import de.arvato.stratego.model.MoveView;
import de.arvato.stratego.model.PiecesView;
import de.arvato.stratego.model.PlayerView;
import de.arvato.stratego.model.TurnView;
import de.arvato.stratego.util.PieceUtil;
import de.arvato.stratego.util.RandomKeyGenerator;
import io.colyseus.Client;
import io.colyseus.Room;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Observable;

public class ColyseusManager extends Observable {

    private static final String TAG = "ColyseusManager";

    private static ColyseusManager instance;

    private Client client;

    private Room<GameState> room;

    private MutableLiveData<PlayerView> playerLiveData;
    private MutableLiveData<PiecesView> piecesLiveData;
    private MutableLiveData<PlayerView> playerReadyLive;
    private MutableLiveData<TurnView> gameStartLive;
    private MutableLiveData<MoveView> moveLiveData;
    private MutableLiveData<String> finishGameLive;
    private MutableLiveData<String> playerDisconnected;

    private ColyseusManager(String serverUrl) {
        client = new Client(serverUrl);
    }

    public static synchronized ColyseusManager getInstance(String serverUrl) {
        if (instance == null) {
            instance = new ColyseusManager(serverUrl);
        }
        return instance;
    }

    public void createPrivateRoom (String name, int color) {
        LinkedHashMap<String, Object> options = new LinkedHashMap<>();
        options.put("name", name);
        options.put("color", color);
        String randomKey = RandomKeyGenerator.generateRandomKey(RandomKeyGenerator.KEY_LENGTH);
        options.put ("key", randomKey);
        client.create (GameState.class, "stratego_game", options, this::clientListeners, Throwable::printStackTrace);
    }

    public void joinOrCreate (String name, int color) {
        LinkedHashMap<String, Object> options = new LinkedHashMap<>();
        options.put("name", name);
        options.put("color", color);
        client.joinOrCreate(GameState.class, "stratego_game", options, this::clientListeners, Throwable::printStackTrace);
    }

    public void join (String name, int color, String roomId, JoinRoomCallback callback) {
        LinkedHashMap<String, Object> options = new LinkedHashMap<>();
        options.put("name", name);
        options.put("color", color); // Color is not relevant when joining as you get the opposite of your enemy

        //client.joinById(GameState.class, roomId, options, this::clientListeners, Throwable::printStackTrace);
        client.joinById(GameState.class, roomId, options,
                room -> {
                    // Success callback
                    if (callback != null) {
                        clientListeners(room);
                        callback.onSuccess(room);
                    }
                },
                throwable -> {
                    // Error callback
                    if (callback != null) {
                        callback.onError(handleError(throwable));
                    }
                }
        );
    }

    private Exception handleError(Throwable throwable) {
        if (throwable instanceof Client.MatchMakeException) {
            Client.MatchMakeException error = (Client.MatchMakeException) throwable;
            switch (error.getCode()) {
                case Client.MatchMakeException.ERR_MATCHMAKE_INVALID_ROOM_ID :
                   return new RoomNotFoundException("Room ID not found!");
            }
        }
        return new GenericColyseusException("Unexpected error: " + throwable.getMessage());
    }

    private void clientListeners(Room<GameState> r) {
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
                if (playerLiveData != null) {
                    playerLiveData.postValue(new PlayerView(player.name, player.color));
                }
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

        r.getState().mapPieces.setOnAdd((pieces, key) -> {
            Log.d(TAG, "Pieces added: " + key + " -> " + pieces);
            if (!key.equals(this.room.getSessionId())) {
                if (piecesLiveData != null) {
                    piecesLiveData.postValue(new PiecesView(pieces.pieces));
                }
            }
        });

        r.getState().mapPieces.setOnRemove((pieces, key) -> {
            Log.d(TAG, "Pieces removed: " + key + " -> " + pieces);
        });

        r.getState().mapPieces.setOnChange((pieces, key) -> {
            Log.d(TAG, "Pieces changed: " + key + " -> " + pieces);
        });

        r.getState().primitives.setOnChange(changes -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                changes.forEach(change -> {
                    Log.d(TAG, "State primitives -->" + change.getField() + ": " + change.getPreviousValue() + " -> " + change.getValue());
                });
            }
        });

            /*r.setOnStateChange((state, isFirstState) -> {
                Log.d(TAG, "onStateChange()");
                Log.d(TAG, "state.primitives.turn = " + state.primitives.turn);
                Log.d(TAG, "isFirstState:" + isFirstState);
            });*/


        //messages listener
        r.onMessage("move", JsonNode.class, (JsonNode message) -> {
            int from = message.get("from").asInt();
            int to = message.get("to").asInt();
            Log.d(TAG, "Move got from enemy:" + "From:" + from + " to:" + to);
            moveLiveData.postValue(new MoveView(from, to));
        });

        r.onMessage("client_left", JsonNode.class, (JsonNode message) -> {
            String sessionId = message.get("sessionId").asText();
            Log.d(TAG, "Client:" + sessionId + " left the room");
            playerDisconnected.postValue("disconnected");
        });

        r.onMessage("ready", Player.class, (Player player) -> {
            Log.d(TAG, "Client is ready:" + player);
            playerReadyLive.postValue(new PlayerView(player.name, player.color));
        });

        r.onMessage("game_start", JsonNode.class, (JsonNode message) -> {
            Log.d(TAG, "Game can start with turn:" + message);
            gameStartLive.postValue (new TurnView(message.asInt()));
        });


        r.onMessage("finish_game", JsonNode.class, (JsonNode message) -> {
            Log.d(TAG, "Finish Game Event");
            finishGameLive.postValue("finished");
        });
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

    /*public void triggerPlayers () {
        room.getState().players.triggerAll();
    }*/

    public Player getEnemyPlayer () {
        Player enemy=null;
        for (Map.Entry<String, Player> entry : room.getState().players.entrySet()) {
            if (!entry.getKey().equals(room.getSessionId())) {
                enemy = entry.getValue();
                break;
            }
        }
        Log.d(TAG, "Exists player enemy in room:" + enemy);
        return enemy;
    }

    public PiecesView getEnemyPieces () {
        PieceArray enemy=null;
        for (Map.Entry<String, PieceArray> entry : room.getState().mapPieces.entrySet()) {
            if (!entry.getKey().equals(room.getSessionId())) {
                enemy = entry.getValue();
                break;
            }
        }
        if (enemy != null) {
            return new PiecesView(enemy.pieces);
        }
        return null;
    }

    public void sendMove(int selectedPos, int index) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.createObjectNode()
                .put("from", selectedPos)
                .put("to", index);

        Log.d(TAG, "Colyseus sending move from:" + selectedPos + " to:" + index);
        room.send("move", jsonNode);
    }

    public void sendFinishGame() {
        room.send("finish_game");
    }

    public void sendFakeMove () throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.createObjectNode()
                .put("from", "32")
                .put("to", "34");
        room.send("fakeMove", jsonNode);
    }

    public void setPieces(Piece[] pieces) {
        String [] piecesArrayString = PieceUtil.transformPiecesToString(pieces);
        room.send ("set_pieces", piecesArrayString);
    }

    public void sendFakeMessage() {
        room.send("fakeMessage", "sending fake message from client:" + this.room.getSessionId());
    }

    public void setPlayerLiveData(MutableLiveData<PlayerView> playerLiveDta) {
        this.playerLiveData = playerLiveDta;
    }

    public void setPiecesLiveData(MutableLiveData<PiecesView> piecesLiveData) {
        this.piecesLiveData = piecesLiveData;
    }

    public void setPlayerReadyLive(MutableLiveData<PlayerView> playerReadyLive) {
        this.playerReadyLive = playerReadyLive;
    }

    public void setGameStartLive(MutableLiveData<TurnView> gameStartLive) {
        this.gameStartLive = gameStartLive;
    }

    public void setMoveLiveData(MutableLiveData<MoveView> moveLiveData) {
        this.moveLiveData = moveLiveData;
    }

    public void setFinishGameLive(MutableLiveData<String> finishGameLive) {
        this.finishGameLive = finishGameLive;
    }

    public void setPlayerDisconnected(MutableLiveData<String> playerDisconnected) {
        this.playerDisconnected = playerDisconnected;
    }
}

