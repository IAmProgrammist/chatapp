package com.example.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class JoinOrCreateRoomActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_or_create_room);
        setTitle("Окно комнаты");
        Button join_room = findViewById(R.id.join_room);
        final Button create_room = findViewById(R.id.login_room);
        join_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(JoinOrCreateRoomActivity.this, LoginRoomListViewActivity.class);
                startActivity(intent);
            }
        });
        create_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(JoinOrCreateRoomActivity.this, CreateRoomActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Вы не можете вернуться на данной стадии.", Toast.LENGTH_SHORT).show();
    }
}
