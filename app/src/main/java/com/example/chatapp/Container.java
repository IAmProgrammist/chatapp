package com.example.chatapp;

public class Container {
    private static String nickname = "";
    private static String login = "";
    private static String password = "";
    private static String room_login = "";
    private static String room_password = "";

    public static String getNickname() {
        return nickname;
    }

    public static void setNickname(String nickname) {
        Container.nickname = nickname;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        Container.password = password;
    }

    public static String getRoom_login() {
        return room_login;
    }

    public static void setRoom_login(String room_login) {
        Container.room_login = room_login;
    }

    public static String getRoom_password() {
        return room_password;
    }

    public static void setRoom_password(String room_password) {
        Container.room_password = room_password;
    }

    public static String getLogin() {
        return login;
    }

    public static void setLogin(String login) {
        Container.login = login;
    }
    public static void nullate(){
        nickname = "";
        login = "";
        password = "";
        room_login = "";
        room_password = "";
    }
}
