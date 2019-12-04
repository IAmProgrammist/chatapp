package com.example.chatapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

import static com.example.chatapp.MainActivity.connection;

public class LoginRoomActivity extends AppCompatActivity {
    List<Object> stuff = new ArrayList<>();

    public static List<Object> getAllThisStuff() {
        List<Object> stuff = new ArrayList<>();
        return null;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_room);
        final EditText login = findViewById(R.id.login_room);
        login.setEnabled(false);
        final EditText password = findViewById(R.id.password_room);
        login.setText(getIntent().getStringExtra("RoomName"));
        Button loginButton = findViewById(R.id.next);
        final Connection connection = Connection.getInstance();
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    HardMessage hmsg = new HardMessage();
                    hmsg.setType(MessageType.HARD_MESSAGE_WITH_ARRAY_OF_LOGIN_ROOM);
                    hmsg.setStuff(new String[]{login.getText().toString(), password.getText().toString()});
                    connection.send(hmsg);
                    Message msg;
                    while (true) {
                        msg = connection.receive();
                        if (msg.getType() == MessageType.CHECK_ROOM_LOGIN_EXISTS) {
                            Intent intent = new Intent(LoginRoomActivity.this, ChatActivitry.class);
                            intent.putExtra("RoomName", msg.getData());
                            startActivity(intent);
                            Container.setRoom_login(login.getText().toString());
                            Container.setRoom_password(password.getText().toString());
                            break;
                        } else if (msg.getType() == MessageType.CHECK_ROOM_LOGIN_DOESNT_EXIST) {
                            TextView textView1 = findViewById(R.id.textView14);
                            textView1.setVisibility(View.VISIBLE);
                            break;
                        }
                    }
                } catch (Exception e) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginRoomActivity.this);
                    builder.setTitle("Ошибка!")
                            .setMessage("Вы отключились!")
                            .setCancelable(false)
                            .setNegativeButton("Перезагрузить приложение",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Intent intent = new Intent(LoginRoomActivity.this, MainActivity.class);
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
                                ActivityCompat.finishAffinity(LoginRoomActivity.this);
                            }
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });

    }

    public void onBackPressed() {
        Intent intent = new Intent(this, CreateRoomActivity.class);
        startActivity(intent);

    }



}
