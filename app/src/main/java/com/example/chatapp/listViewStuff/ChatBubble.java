package com.example.chatapp.listViewStuff;

import java.util.Date;

public class ChatBubble {

    private String nickname;
    private String msg;
    private Date date;
    private boolean myMessage;

    public ChatBubble(String nickname, String msg, boolean myMessage) {
        this.nickname = nickname;
        this.msg = msg;
        this.myMessage = myMessage;
        date = new Date();
    }
    public ChatBubble(String nickname, String msg, boolean myMessage, long time) {
        this.nickname = nickname;
        this.msg = msg;
        this.myMessage = myMessage;
        date = new Date(time);
    }

    public String getNickname() {
        return nickname;
    }

    public String getMsg() {
        return msg;
    }

    public String getDate() {
        String result = String.format("%d:%d", date.getHours(), date.getMinutes());
        return result;
    }
    private static Integer getMilliseconds(Date date){
        int n = (int) (date.getTime() % 1000);
        return n<0 ? n+1000 : n;
    }

    public boolean isMyMessage() {
        return myMessage;
    }
}
