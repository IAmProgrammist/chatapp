package com.example.chatapp;

import android.os.Build;

import androidx.annotation.RequiresApi;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Room {
    private String name;
    private boolean password;
    private Integer users;

    public Room(String name, boolean password, Integer users) {
        this.name = name;
        this.password = password;
        this.users = users;
    }

    public String getName() {
        return name;
    }

    public boolean isPassword() {
        return password;
    }

    public Integer getUsers() {
        return users;
    }
}
