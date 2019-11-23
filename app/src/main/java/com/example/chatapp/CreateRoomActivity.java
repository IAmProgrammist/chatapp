package com.example.chatapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import static com.example.chatapp.MainActivity.connection;

public class CreateRoomActivity extends AppCompatActivity {
    EditText login;
    EditText password;
    Connection connection = Connection.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_room);
        Button createButton = findViewById(R.id.create_room_button);
        login = findViewById(R.id.login_room);
        password = findViewById(R.id.password_room);
        connection = Connection.getInstance();
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!password.getText().toString().equals("") || login.getText().toString().length() < 4) {
                    if (login.getText().toString().length() < 4 || password.getText().toString().length() < 4) {
                        if (login.getText().toString().length() < 4) {
                            login.setError("Название комнаты слишком коротко!");
                        }
                        if (password.getText().toString().length() < 4) {
                            password.setError("Пароль слишком короткий!");
                        }
                    } else {
                        doThatStuff();
                    }
                } else {
                    doThatStuff();
                }
            }
        });

    }

    public void doThatStuff() {
        try {
            HardMessage hmsg = new HardMessage();
            hmsg.setType(MessageType.HARD_MESSAGE_WITH_ARRAY_OF_REGISTER_ROOM);
            hmsg.setStuff(new String[]{login.getText().toString(), password.getText().toString()});
            connection.send(hmsg);
            Message msg;
            while (true) {
                msg = connection.receive();
                if (msg.getType() == MessageType.ROOM_CREATE_EXISTS) {
                    login.setError("Комната уже существует!");
                } else if (msg.getType() == MessageType.CREATE_ROOM_OKAY) {
                    Container.setRoom_login(login.getText().toString());
                    Container.setRoom_password(password.getText().toString());
                    Intent intent = new Intent(CreateRoomActivity.this, ChatActivitry.class);
                    intent.putExtra("RoomName", login.getText().toString());
                    startActivity(intent);
                    break;
                }
            }
        } catch (Exception e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateRoomActivity.this);
            builder.setTitle("Ошибка!")
                    .setMessage("Вы отключились!")
                    .setCancelable(false)
                    .setNegativeButton("Перезагрузить приложение",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent intent = new Intent(CreateRoomActivity.this, MainActivity.class);
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
                        ActivityCompat.finishAffinity(CreateRoomActivity.this);
                    }
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    public void onBackPressed() {
        Intent intent = new Intent(this, JoinOrCreateRoomActivity.class);
        startActivity(intent);

    }
}
