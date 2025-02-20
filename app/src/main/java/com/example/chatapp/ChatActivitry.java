package com.example.chatapp;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.chatapp.listViewStuff.ChatAdapter;
import com.example.chatapp.listViewStuff.ChatBubble;

import org.json.JSONException;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static androidx.core.app.NotificationCompat.PRIORITY_DEFAULT;
import static androidx.core.app.NotificationCompat.PRIORITY_HIGH;
import static com.example.chatapp.MainActivity.connection;

public class ChatActivitry extends AppCompatActivity {
    public static List<ChatBubble> bubbles;
    public static ArrayAdapter<ChatBubble> chatBubbleArrayAdapter;
    public static String res_trans = Container.getNickname();
    public static Activity staticActivity;
    public static List<String> users;
    public static TextView curUsers;
    public static Activity me;
    public static ListView listView;
    EditText editText;
    public void lol(){
        me = this;
    }

    public void setStaticActivity() {
        staticActivity = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SuperDuperService.shouldContinue = false;
        lol();
        Container.setLaunched(true);
        super.onCreate(savedInstanceState);
        String chat_name = getIntent().getExtras().getString("RoomName");
        setStaticActivity();
        setTitle(chat_name);
        setContentView(R.layout.activity_main);
        curUsers = findViewById(R.id.curUsers);
        listView = findViewById(R.id.chatListView);
        ImageButton sendButton = (ImageButton) findViewById(R.id.button);
        editText = findViewById(R.id.TextInputEditTextRoar);
        bubbles = new ArrayList<>();
        chatBubbleArrayAdapter = new ChatAdapter(this, R.layout.my_buble, bubbles);
        listView.setAdapter(chatBubbleArrayAdapter);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!editText.getText().toString().trim().equals("")) {
                        Container.setLaunched(true);
                        if (msg_pressed + 2000 < System.currentTimeMillis()) {
                            Connection connection = Connection.getInstance();
                            HardMessage message = new HardMessage();
                            message.setType(MessageType.TEXT);
                            message.setData(editText.getText().toString().trim());
                            editText.setText("");
                            connection.send(message);
                            msg_pressed = System.currentTimeMillis();
                        } else {
                            Toast.makeText(getBaseContext(), "Вы отправляете сообщения слишком часто!", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        editText.setText("");
                        Container.setLaunched(true);
                    }
                } catch (Exception e) {
                    if (Container.isLaunched()) {
                        if (Container.getLogin().equals("") && Container.getPassword().equals("")) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivitry.this);
                            builder.setTitle("Ошибка!")
                                    .setMessage("Потеря соединения с сервером!")
                                    .setCancelable(false)
                                    .setNegativeButton("Перезагрузить приложение",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    Container.nullate();
                                                    Intent intent = new Intent(ChatActivitry.this, MainActivity.class);
                                                    startActivity(intent);
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
                                        finishAffinity();
                                    } else {
                                        ActivityCompat.finishAffinity(ChatActivitry.this);
                                    }
                                }
                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivitry.this);
                            builder.setTitle("Ошибка!")
                                    .setMessage("Потеря соединения с сервером!")
                                    .setCancelable(false)
                                    .setNegativeButton("Перезагрузить приложение",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    Container.nullate();
                                                    Intent intent = new Intent(ChatActivitry.this, MainActivity.class);
                                                    startActivity(intent);
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
                                        finishAffinity();
                                    } else {
                                        ActivityCompat.finishAffinity(ChatActivitry.this);
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
                                    Intent intent = new Intent(ChatActivitry.this, MainActivity.class);
                                    startActivity(intent);
                                }
                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                    } else {
                        HardMessage message1 = new HardMessage();
                        message1.setType(MessageType.EXIT_PROGRAM);
                        try {
                            connection.send(message1);
                        } catch (Exception e1) {

                        }
                        Intent intent = new Intent(ChatActivitry.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });
        Intent chat = new Intent(this, SuperDuperService.class);
        SuperDuperService.shouldContinue = true;
        startService(chat);
        Toast.makeText(this, res_trans, Toast.LENGTH_SHORT);
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static void notificate(String title, String message){
        Intent resultIntent = me.getIntent();
        NotificationManager notificationManager = (NotificationManager) me.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(me.getApplicationContext(), "Chat channel only for RChat")
                        .setAutoCancel(false)
                        .setSmallIcon(R.drawable.logo_logo)
                        .setWhen(System.currentTimeMillis())
                        .setContentTitle(title)
                        .setContentText(message)
                        .setPriority(PRIORITY_DEFAULT);
        createChannelIfNeeded(notificationManager);
        notificationManager.notify(101, notificationBuilder.build());
    }
    public static void createChannelIfNeeded(NotificationManager manager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("Chat channel only for RChat", "Chat channel only for RChat", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(notificationChannel);
        }
    }



    @Override
    protected void onDestroy() {
        Client.destroy = true;
        HardMessage message1 = new HardMessage();
        message1.setType(MessageType.EXIT_PROGRAM);
        try {
            connection.send(message1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.quit) {
            Client.destroy = true;
            HardMessage message1 = new HardMessage();
            message1.setType(MessageType.EXIT_PROGRAM);
            try {
                connection.send(message1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                finishAffinity();
                Container.nullate();
            } else {
                Container.nullate();
                ActivityCompat.finishAffinity(this);
            }
        } else if (id == R.id.change) {
            HardMessage message1 = new HardMessage();
            message1.setType(MessageType.I_WANNA_RELOGIN);
            try {
                connection.send(message1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Client.destroy = true;
            Intent intent = new Intent(this, JoinOrCreateRoomActivity.class);
            startActivity(intent);
        } else if (id == R.id.usrs) {
            users = Client.users;
            String msg = "";
            for (String j : users) {
                msg += j + "\n";
            }
            final AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivitry.this);
            builder.setTitle("Пользователи:").setMessage(msg).setCancelable(true).setNegativeButton("Ок", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
        return super.onOptionsItemSelected(item);
    }


    private static long back_pressed;
    private static long msg_pressed;


    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()) {
            HardMessage message1 = new HardMessage();
            message1.setType(MessageType.I_WANNA_RELOGIN);
            try {
                connection.send(message1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Client.destroy = true;
            Intent intent = new Intent(this, JoinOrCreateRoomActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(getBaseContext(), "Нажмите кнопку 'назад' ещё раз, чтобы выйти из комнаты!", Toast.LENGTH_SHORT).show();
        }
        back_pressed = System.currentTimeMillis();
    }

    @Override
    protected void onPause() {
        Container.setLaunched(false);
        super.onPause();
    }

    @Override
    protected void onResume() {
        Container.setLaunched(true);
        super.onResume();
    }

    @Override
    protected void onStop() {
        Container.setLaunched(false);
        super.onStop();
    }
}

