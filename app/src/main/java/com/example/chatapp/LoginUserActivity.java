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

public class LoginUserActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_user);
        setTitle("Войти в пользователя");
        Button button5 = findViewById(R.id.button5);
        final Connection connection = Connection.getInstance();
        final CheckBox check = findViewById(R.id.remember);
        final EditText tiet1 = findViewById(R.id.login_login);
        final EditText tiet21 = findViewById(R.id.login_password);
        if(SaveSharedPreference.getUserName(LoginUserActivity.this).length() != 0){
            tiet1.setText(SaveSharedPreference.getUserName(LoginUserActivity.this));
        }
        if(SaveSharedPreference.getPassword(LoginUserActivity.this).length() != 0){
            tiet21.setText(SaveSharedPreference.getPassword(LoginUserActivity.this));
        }
        if(SaveSharedPreference.getRemember(LoginUserActivity.this)){
            check.setChecked(true);
        }else{
            check.setChecked(false);
        }
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    HardMessage hmsg = new HardMessage();
                    hmsg.setType(MessageType.HARD_MESSAGE_WITH_ARRAY_OF_LOGIN_USER);
                    hmsg.setStuff(new String[]{tiet1.getText().toString(), tiet21.getText().toString()});
                    connection.send(hmsg);
                    Message msg;
                    while (true) {
                        msg = connection.receive();
                        if (msg.getType() == MessageType.CHECK_USER_LOGIN_NO) {
                            tiet1.setError("Логин или пароль введены неверно");
                            tiet21.setError("Логин или пароль введены неверно");
                            break;
                        } else if (msg.getType() == MessageType.CHECK_USER_LOGIN_YES) {

                            if(check.isChecked()){
                                SaveSharedPreference.setUserName(LoginUserActivity.this, tiet1.getText().toString());
                                SaveSharedPreference.setPassword(LoginUserActivity.this, tiet21.getText().toString());
                                SaveSharedPreference.setRemember(LoginUserActivity.this, true);
                            }else{
                                SaveSharedPreference.setUserName(LoginUserActivity.this, "");
                                SaveSharedPreference.setPassword(LoginUserActivity.this, "");
                                SaveSharedPreference.setRemember(LoginUserActivity.this, false);
                            }
                            Container.setLogin(tiet1.getText().toString());
                            Container.setPassword(tiet21.getText().toString());
                            Intent intent = new Intent(LoginUserActivity.this, JoinOrCreateRoomActivity.class);
                            Container.setNickname(msg.getData());
                            startActivity(intent);
                            break;
                        }
                    }
                } catch (Exception e) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginUserActivity.this);
                    builder.setTitle("Ошибка!")
                            .setMessage("Вы отключились!")
                            .setCancelable(false)
                            .setNegativeButton("Перезагрузить приложение",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Intent intent = new Intent(LoginUserActivity.this, MainActivity.class);
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
                                ActivityCompat.finishAffinity(LoginUserActivity.this);
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
        Intent intent = new Intent(LoginUserActivity.this, LoginOrCreateUserActivity.class);
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
