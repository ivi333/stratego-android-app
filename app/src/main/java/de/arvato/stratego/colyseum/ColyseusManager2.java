//package de.arvato.stratego.colyseum;
//
//import io.colyseus.Client;
//import io.colyseus.Room;
//import io.colyseus.serializer.SchemaSerializer;
//
//
//public class ColyseusManager2 {
//
//    private static ColyseusManager2 instance;
//    private Client client;
//    private Room<ChatState> room;
//
//    // Private constructor to enforce singleton pattern
//    private ColyseusManager2() {
//        client = new Client("ws://localhost:2567");
//    }
//
//    // Public method to provide access to the singleton instance
//    public static ColyseusManager2 getInstance() {
//        if (instance == null) {
//            instance = new ColyseusManager2();
//        }
//        return instance;
//    }
//
//    // Connect to the room
//    /*public void connectToRoom(String roomName) {
//        try {
//            room = client.joinOrCreate(roomName, new SchemaSerializer(), ChatState.class);
//
//            room.onJoin((roomInstance) -> {
//                System.out.println("Joined successfully.");
//                // Optionally, you can add more event listeners here
//            });
//
//            room.onMessage((type, message) -> {
//                // Handle messages received from the server
//                System.out.println("Received message: " + message);
//            });
//
//            room.onLeave((code) -> {
//                System.out.println("Left the room with code: " + code);
//            });
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }*/
//
//    // Connect to the room
//    public void connectToRoom(String roomName) {
//
//        client.joinOrCreate(roomName, ChatState.class).thenAccept(room -> {
//            this.room = room;
//            System.out.println("Joined successfully.");
//
//
//            // Optionally, add more event listeners here
//            room.onMessage((type, message) -> {
//                // Handle messages received from the server
//                System.out.println("Received message: " + message);
//            });
//
//            room.state.onChange((state, isFirstState) -> {
//                // Handle state changes
//                System.out.println("State has changed: " + state);
//            });
//
//            room.onLeave(code -> {
//                System.out.println("Left the room with code: " + code);
//            });
//
//        }).exceptionally(e -> {
//            e.printStackTrace();
//            return null;
//        });
//    }
//
//
//    // Send a message to the server
//    public void sendMessage(String type, Object message) {
//        if (room != null) {
//            room.send(type, message);
//        }
//    }
//
//    // Example state change listener
//    public void onStateChange() {
//        room.onStateChange((state, isFirstState) -> {
//            System.out.println("State has changed: " + state);
//        });
//    }
//}