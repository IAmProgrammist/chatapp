package com.example.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginOrCreateUserActivity extends AppCompatActivity {
    private Button button2;
    private Button button4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_or_register);
        setTitle("Окно пользователя");
        button2 = (Button) findViewById(R.id.join_room);
        button4 = (Button) findViewById(R.id.login_room);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginOrCreateUserActivity.this, LoginUserActivity.class);
                startActivity(intent);
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginOrCreateUserActivity.this, CreateUserActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Вы не можете вернуться на данной стадии.", Toast.LENGTH_SHORT).show();

    }
}
