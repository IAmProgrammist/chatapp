package com.example.chatapp;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {
    private MessageType type;
    private String data;
    private String roomId;
    private String sender;
    private Date date;

    public Message() {
        type = null;
        data = null;
        roomId = null;
        date = null;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public MessageType getType() {
        return type;
    }

    public String getData() {
        return data;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = new Date(date);
    }
}
