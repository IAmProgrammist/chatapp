package com.example.chatapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import static com.example.chatapp.MainActivity.connection;

public class CreateUserActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_user);
        setTitle("Создать пользователя");
        Button button6 = findViewById(R.id.button6);
        final EditText login = findViewById(R.id.register_login);
        final EditText password = findViewById(R.id.register_password);
        final EditText nickname = findViewById(R.id.register_nickname);
        final String pat = "(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9@#$%]).{8,}";
        final CheckBox checkBox = findViewById(R.id.checkBoxReg);
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (login.getText().toString().length() < 4 || !password.getText().toString().matches(pat) || nickname.getText().toString().length() < 4 || nickname.getText().toString().contains(":")) {
                        if (login.getText().toString().length() < 4) {
                            login.setError("Логин слишком короткий, он должен содержать как минимум 4 символа");
                        }
                        if (!password.getText().toString().matches(pat)) {
                            password.setError("Пароль слишком легкий");
                        }
                        if (nickname.getText().toString().length() < 4) {
                            nickname.setError("Никнейм слишком короткий, он должен содержать как минимум 4 символа");
                        }
                        if (nickname.getText().toString().contains(":")) {
                            nickname.setError("Ваш ник не должен содержать символ ':'");
                        }
                    } else {
                        HardMessage hmsg = new HardMessage();
                        hmsg.setType(MessageType.HARD_MESSAGE_WITH_ARRAY_OF_REGISTER_USER);
                        hmsg.setStuff(new String[]{login.getText().toString(), password.getText().toString(), nickname.getText().toString()});
                        connection.send(hmsg);
                        Message msg;
                        while (true) {
                            msg = connection.receive();
                            if (msg.getType() == MessageType.CHECK_USER_REGISTER_YES) {
                                if (checkBox.isChecked()) {
                                    SaveSharedPreference.setRemember(CreateUserActivity.this, true);
                                    SaveSharedPreference.setUserName(CreateUserActivity.this, login.getText().toString());
                                    SaveSharedPreference.setPassword(CreateUserActivity.this, password.getText().toString());
                                } else {
                                    SaveSharedPreference.setRemember(CreateUserActivity.this, false);
                                }
                                Container.setLogin(login.getText().toString());
                                Intent intent = new Intent(CreateUserActivity.this, JoinOrCreateRoomActivity.class);
                                Container.setNickname(msg.getData());
                                startActivity(intent);
                                break;
                            } else if (msg.getType() == MessageType.CHECK_USER_REGISTER_NO) {
                                login.setError("Аккаунт уже существует");
                                nickname.setError("Аккаунт уже существует");
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CreateUserActivity.this);
                    builder.setTitle("Ошибка!")
                            .setMessage("Вы отключились!")
                            .setCancelable(false)
                            .setNegativeButton("Перезагрузить приложение",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Intent intent = new Intent(CreateUserActivity.this, MainActivity.class);
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
                                ActivityCompat.finishAffinity(CreateUserActivity.this);
                            }
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(CreateUserActivity.this, LoginOrCreateUserActivity.class);
        startActivity(intent);
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

    @Override
    protected void onStart() {
        Container.setLaunched(true);
        super.onStart();
    }
}
