package com.example.chatapp;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;


public class Connection implements Closeable, Serializable{
    private final Socket socket;
    private final BufferedReader reader;
    private final PrintWriter writer;
    private static Connection connection;
    private static Activity activity = MainActivity.staticActivity;
    public static void createInstance(Socket socket) throws IOException {
        connection = new Connection(socket);
    }
    public static Connection getInstance(){
        return connection;
    }

    private Connection(Socket socket) throws IOException {
        this.socket = socket;
        writer = new PrintWriter(socket.getOutputStream(), true);
        writer.flush();
        InputStream in1 = socket.getInputStream();
        reader = new BufferedReader(new InputStreamReader(in1));
    }

    public void send(HardMessage message) throws IOException, JSONException {
        synchronized (writer) {
            if (isOnline() && check("82.151.126.74", 2156)) {
                if (message.getStuff() == null) {
                    JSONObject messageJSON = new JSONObject();
                    messageJSON.put("type", message.getType().toString());
                    if (message.getData() != null) {
                        messageJSON.put("data", message.getData());
                    }
                    if (message.getRoomId() != null) {
                        messageJSON.put("roomid", message.getRoomId());
                    }
                    if(message.getSender() != null){
                        messageJSON.put("sender", message.getSender());
                    }
                    writer.println(messageJSON.toString());
                } else {
                    JSONObject messageJSON = new JSONObject();
                    messageJSON.put("type", message.getType().toString());
                    if (message.getData() != null) {
                        messageJSON.put("data", message.getData());
                    }
                    if (message.getRoomId() != null) {
                        messageJSON.put("roomid", message.getRoomId());
                    }
                    if(message.getSender() != null){
                        messageJSON.put("sender", message.getSender());
                    }
                    message = (HardMessage) message;
                    JSONArray jsonArray = new JSONArray();
                    for (String j : ((HardMessage) message).getStuff()) {
                        jsonArray.put(j);
                    }
                    messageJSON.put("array", jsonArray);
                    writer.println(messageJSON.toString());
                }
            }else{
                throw new IOException("Connection interrupted");
            }
        }
    }
    public List<Room> receiveRooms() throws IOException, JSONException {
        String result = "";
        synchronized (reader) {
            if (isOnline() && check("82.151.126.74", 2156)) {
                String userInput = "";
                while (true) {
                    try {
                        if (socket.isConnected() && !(userInput = reader.readLine()).equals("\\n")) {
                            result += userInput;
                            break;
                        } else if (userInput.equals("\\n")) {
                            ;
                            ;
                        } else {
                            Thread.sleep(1);
                        }
                    } catch (Exception e) {
                    }
                }
                JSONObject json = new JSONObject(result);
                if(MessageType.valueOf((String) json.get("type")) == MessageType.HARD_MESSAGE_WITH_ARRAY_OF_ROOMS){
                    try {
                        JSONArray rooms = (JSONArray) json.get("array");
                        Integer r = rooms.length();
                        List<JSONObject> objects = new ArrayList<>();
                        try {
                            for (int i = 0; i < rooms.length(); i++) {
                                objects.add(rooms.getJSONObject(i));
                            }
                        }catch (Exception e){

                        }
                        List<Room> finRoom = new ArrayList<>();
                        for (JSONObject obj : objects) {
                            finRoom.add(new Room(obj.optString("name"), obj.optBoolean("lock"), obj.optInt("usersnum")));
                        }
                        return finRoom;
                    }catch (Exception e){
                        e = e;
                        return null;
                    }

                }else{

                    throw new IOException();
                }
            }else{
                throw new IOException();
            }
        }
    }
    public List<Message> recieveHistory() throws JSONException, IOException {
        String result = "";
        synchronized (reader) {
            if (isOnline() && check("82.151.126.74", 2156)) {
                String userInput = "";
                while (true) {
                    try {
                        if (socket.isConnected() && !(userInput = reader.readLine()).equals("\\n")) {
                            result += userInput;
                            break;
                        } else if (userInput.equals("\\n")) {
                            ;
                            ;
                        } else {
                            Thread.sleep(1);
                        }
                    } catch (Exception e) {
                    }
                }
                JSONObject json = new JSONObject(result);
                if(MessageType.valueOf(String.valueOf(json.get("type"))) == MessageType.HISTORY){
                    JSONArray array = json.getJSONArray("array");
                    List<Message> msg = new ArrayList<>();
                    for(int i = 0; i < array.length(); i++){
                        Message tmpMessage = new Message();
                        tmpMessage.setType(MessageType.valueOf(array.getJSONObject(i).getString("type")));
                        tmpMessage.setSender(array.getJSONObject(i).getString("sender"));
                        if(array.getJSONObject(i).has("data")){
                            tmpMessage.setData(array.getJSONObject(i).getString("data"));
                        }
                        msg.add(tmpMessage);
                    }
                    return msg;
                }else{
                    throw new IOException();
                }
            }
        }
        return null;
    }

    public Message receive() throws IOException{
        String result = "";
        synchronized (reader) {
            try {
                String userInput = "";
                while (true) {
                    if (socket.isConnected() && !(userInput = reader.readLine()).equals("\\n")) {
                        result += userInput;
                        break;
                    }else if(userInput.equals("\\n")){
                        ;;
                    } else {
                        Thread.sleep(1);
                    }
                }
                JSONObject json = new JSONObject(result);
                if(MessageType.valueOf(json.getString("type")) != MessageType.HISTORY) {
                    if (json.has("array")) {
                        HardMessage hardMessage = new HardMessage();
                        List<String> lol = new ArrayList<>();
                        JSONArray array = (JSONArray) json.get("array");
                        for (int i = 0; i < array.length(); i++) {
                            lol.add((String) array.get(i));
                        }
                        hardMessage.setType(MessageType.valueOf((String) json.get("type")));
                        hardMessage.setStuff(lol.toArray(new String[0]));
                        if (json.has("data")) {
                            hardMessage.setData((String) json.get("data"));
                        }
                        if (json.has("roomid")) {
                            hardMessage.setRoomId((String) json.get("roomid"));
                        }
                        if (json.has("sender")) {
                            hardMessage.setSender((String) json.get("sender"));
                        }
                        return hardMessage;
                    } else {
                        HardMessage message = new HardMessage();
                        message.setType(MessageType.valueOf((String) json.get("type")));
                        if (json.has("data")) {
                            message.setData((String) json.get("data"));
                        }
                        if (json.has("roomid")) {
                            message.setRoomId((String) json.get("roomid"));
                        }
                        if (json.has("sender")) {
                            message.setSender((String) json.get("sender"));
                        }
                        return message;
                    }
                }
            }catch (InterruptedException e) {
                e.printStackTrace();
                e.printStackTrace();
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public SocketAddress getRemoteSocketAddress(){
        return this.socket.getRemoteSocketAddress();
    }
    private boolean check(String address, int port) {

        try {
            Socket soc = new Socket();
            soc.setSoTimeout(2000);
            soc.connect(new InetSocketAddress(address, port));
            soc.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void close() throws IOException{
        writer.close();
        reader.close();
        this.socket.close();
    }
    protected boolean isOnline() {
        String cs = Context.CONNECTIVITY_SERVICE;
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(cs);
        if (cm.getActiveNetworkInfo() == null) {
            return false;
        } else {
            return true;
        }
    }
}