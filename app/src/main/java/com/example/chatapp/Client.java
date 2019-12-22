package com.example.chatapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.chatapp.listViewStuff.ChatBubble;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;

public class Client implements Runnable {
    public static List<ChatBubble> bubbles;
    public static ArrayAdapter<ChatBubble> chatBubbleArrayAdapter;
    private static Activity activity;
    public static List<String> users;
    public static TextView txtView;
    public static List<Message> messages;
    public static Object lock = new Object();
    public static boolean destroy;
    public static ListView listView;
    private static final int NOTIFY_ID = 213;
    private static String CHANNEL_ID = "Chat channel";

    @Override
    public void run() {
        listView = ChatActivitry.listView;
        destroy = false;
        activity = ChatActivitry.staticActivity;
        users = new ArrayList<>();
        bubbles = ChatActivitry.bubbles;
        chatBubbleArrayAdapter = ChatActivitry.chatBubbleArrayAdapter;
        final Connection connection = Connection.getInstance();
        txtView = ChatActivitry.curUsers;
        try {
            messages = connection.recieveHistory();
            for (final Message message : messages) {
                if (message.getType() == MessageType.TEXT) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            synchronized (lock) {
                                ChatBubble chatBubble;
                                if (message.getSender().equals(ChatActivitry.res_trans)) {
                                    chatBubble = new ChatBubble(message.getSender(), message.getData(), true, message.getDate().getTime());
                                } else {
                                    chatBubble = new ChatBubble(message.getSender(), message.getData(), false, message.getDate().getTime());

                                }
                                bubbles.add(chatBubble);
                                chatBubbleArrayAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                } else if (message.getType() == MessageType.USER_ADDED) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            synchronized (lock) {
                                ChatBubble cuccle = null;
                                if (message.getSender().equals(ChatActivitry.res_trans)) {
                                    cuccle = new ChatBubble(message.getSender(), message.getSender() + " присоединился к чату.", true, message.getDate().getTime());
                                } else {
                                    cuccle = new ChatBubble(message.getSender(), message.getSender() + " присоединился к чату.", false, message.getDate().getTime());
                                }
                                //bubbles.add(cuccle);
                                //chatBubbleArrayAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                } else if (message.getType() == MessageType.USER_REMOVED) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            synchronized (lock) {
                                ChatBubble cuccle = null;
                                if (message.getSender().equals(ChatActivitry.res_trans)) {
                                    cuccle = new ChatBubble(message.getSender(), message.getSender() + " вышел из чата.", true, message.getDate().getTime());
                                } else {
                                    cuccle = new ChatBubble(message.getSender(), message.getSender() + " вышел из чата.", false, message.getDate().getTime());
                                }
                                //bubbles.add(cuccle);
                                //chatBubbleArrayAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
            }
        } catch (JSONException e) {

        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            while (true) {
                final HardMessage message = (HardMessage) connection.receive();
                if (Thread.currentThread().isInterrupted()) {
                    return;
                } else if (message.getType() == MessageType.TEXT) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                        @Override
                        public void run() {
                            synchronized (lock) {
                                ChatBubble chatBubble;
                                if (message.getSender().equals(ChatActivitry.res_trans)) {
                                    chatBubble = new ChatBubble(message.getSender(), message.getData(), true);
                                } else {
                                    chatBubble = new ChatBubble(message.getSender(), message.getData(), false);
                                }
                                bubbles.add(chatBubble);
                                chatBubbleArrayAdapter.notifyDataSetChanged();
                                boolean m = Container.isLaunched();
                                if(!Container.isLaunched()){
                                    ChatActivitry.notificate(message.getSender(), message.getData());
                                }
                            }
                        }
                    });
                } else if (message.getType() == MessageType.USER_ADDED) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            synchronized (lock) {

                                if (!message.getSender().equals(ChatActivitry.res_trans) && !users.contains(message.getSender())) {
                                    ChatBubble cuccle = null;
                                    if (message.getSender().equals(ChatActivitry.res_trans)) {
                                        cuccle = new ChatBubble(message.getSender(), message.getSender() + " присоединился к чату.", true);
                                    } else {
                                        cuccle = new ChatBubble(message.getSender(), message.getSender() + " присоединился к чату.", false);
                                    }
                                    bubbles.add(cuccle);
                                    chatBubbleArrayAdapter.notifyDataSetChanged();
                                    users.add(message.getSender());
                                    if(users.size() == 1){
                                        txtView.setText("В комнате только вы");
                                    }else {
                                        txtView.setText("В комнате " + users.size() + " " + speakInRussian(users.size()));
                                    }
                                }else if(!users.contains(message.getSender())){
                                    ChatBubble cuccle = null;
                                    if (message.getSender().equals(ChatActivitry.res_trans)) {
                                        cuccle = new ChatBubble(message.getSender(), message.getSender() + " присоединился к чату.", true);
                                    } else {
                                        cuccle = new ChatBubble(message.getSender(), message.getSender() + " присоединился к чату.", false);
                                    }
                                    bubbles.add(cuccle);
                                    chatBubbleArrayAdapter.notifyDataSetChanged();
                                    users.add(message.getSender());
                                    if(users.size() == 1){
                                        txtView.setText("В комнате только вы");
                                    }else {
                                        txtView.setText("В комнате " + users.size() + " " + speakInRussian(users.size()));
                                    }
                                }
                            }
                        }
                    });
                } else if (message.getType() == MessageType.USER_REMOVED) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            synchronized (lock) {
                                if (users.contains(message.getSender())) {
                                    ChatBubble cuccle = null;
                                    if (message.getSender().equals(ChatActivitry.res_trans)) {
                                        cuccle = new ChatBubble(message.getSender(), message.getSender() + " вышел из чата.", true);
                                    } else {
                                        cuccle = new ChatBubble(message.getSender(), message.getSender() + " вышел из чата.", false);
                                    }
                                    bubbles.add(cuccle);
                                    chatBubbleArrayAdapter.notifyDataSetChanged();
                                    users.remove(message.getSender());
                                    if(users.size() == 1){
                                        txtView.setText("В комнате только вы");
                                    }else {
                                        txtView.setText("В комнате " + users.size() + " " + speakInRussian(users.size()));
                                    }
                                }
                            }
                        }
                    });
                } else if (message.getType() == MessageType.YES_YOU_CAN) {
                    break;
                } else if (message.getType() == MessageType.RELOGIN_ROOM) {
                    break;
                } else if (message.getType() == MessageType.USERS_LIST) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            synchronized (lock) {
                                for (String j : message.getStuff()) {
                                    if(!users.contains(j)) {
                                        users.add(j);
                                    }
                                }
                                if(users.size() == 1){
                                    txtView.setText("В комнате только вы");
                                }else {
                                    txtView.setText("В комнате " + users.size() + " " + speakInRussian(users.size()));
                                }
                            }
                        }
                    });
                } else if (message.getType().equals(MessageType.CHECK_CONN)) {
                    HardMessage mj = new HardMessage();
                    mj.setType(MessageType.CONN_CONN);
                    connection.send(mj);
                } else if(message.getType().equals(MessageType.HISTORY_END)) {
                    destroy = true;
                }else{
                    throw new IOException();
                }
            }
        } catch (IOException e) {
            if (Container.isLaunched()) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setTitle("Ошибка!")
                                .setMessage("Вы отключились!")
                                .setCancelable(false)
                                .setNegativeButton("Перезагрузить приложение",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                Container.nullate();
                                                Intent intent = new Intent(activity, MainActivity.class);
                                                activity.startActivity(intent);
                                                HardMessage message1 = new HardMessage();
                                                message1.setType(MessageType.EXIT_PROGRAM);
                                                try {
                                                    connection.send(message1);
                                                } catch (Exception e) {

                                                }
                                            }
                                        }).setNeutralButton("Выйти из приложения", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    activity.finishAffinity();
                                    Container.nullate();
                                } else {
                                    ActivityCompat.finishAffinity(activity);
                                    Container.nullate();
                                }
                            }
                        }).setPositiveButton("Перезайти в комнату", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                HardMessage message1 = new HardMessage();
                                message1.setType(MessageType.EXIT_PROGRAM);
                                try {
                                    connection.send(message1);
                                } catch (Exception e) {

                                }
                                Intent intent = new Intent(activity, MainActivity.class);
                                activity.startActivity(intent);
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();

                    }

                });
            } else {
                HardMessage message1 = new HardMessage();
                message1.setType(MessageType.EXIT_PROGRAM);
                try {
                    connection.send(message1);
                } catch (Exception e1) {

                }
                while (true) {
                    if (Container.isLaunched()) {
                        Intent intent = new Intent(activity, MainActivity.class);
                        activity.startActivity(intent);
                        break;
                    }
                }
            }
        } catch (JSONException e) {

        }
    }


    public String cutUser(String message) {
        String[] h = message.split(":");
        return h[0];
    }

    public String cutMessage(String message) {
        String res = "";
        String[] h = message.split(":");
        for (int i = 1; i < h.length; i++) {
            if (h.length - 1 == i) {
                res += h[i];
            } else {
                res += h[i];
                res += ":";
            }
        }
        return res;
    }
    public String speakInRussian(int num)
    {
        int preLastDigit = num % 100 / 10;
        if (preLastDigit == 1)
        {
            return "пользователей";
        }

        switch (num % 10)
        {
            case 1:
                return "пользователь";
            case 2:
            case 3:
            case 4:
                return "пользователя";
            default:
                return "пользователей";
        }
    }
}
