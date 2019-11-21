package com.example.chatapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.chatapp.listViewStuff.ListViewAdapter;
import com.example.chatapp.listViewStuff.Model;

import java.util.ArrayList;
import java.util.List;

import static com.example.chatapp.MainActivity.connection;

public class LoginRoomListViewActivity extends AppCompatActivity {
    private ListView listView;
    ListViewAdapter adapter;
    ArrayList<Model> arrayList = new ArrayList<>();
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.room_login_listview);
            setTitle("Доступные комнаты");
            listView = findViewById(R.id.listView);
            HardMessage tmpMessage = new HardMessage();
            tmpMessage.setType(MessageType.LOGIN_ROOM_IN_CHECK);
            connection.send(tmpMessage);
            List<Room> rooms = connection.receiveRooms();
            List<String> titles = new ArrayList<>();
            List<String> descs = new ArrayList<>();
            List<Integer> icons = new ArrayList<>();
            for(Room m: rooms){
                titles.add(m.getName());
                if(m.getUsers() == 0){
                    descs.add("В комнате нет пользователей");
                }else{
                    descs.add("В комнате " + m.getUsers() + " пользователей");
                }
                if(m.isPassword()){
                    icons.add(R.drawable.lock);
                }else{
                    icons.add(R.drawable.unlock);
                }
            }
            for(int i = 0; i < titles.size(); i++){
                Model model = new Model(titles.get(i), descs.get(i), icons.get(i));
                arrayList.add(model);
            }
            adapter = new ListViewAdapter(this, arrayList);
            listView.setAdapter(adapter);
        }catch (Exception e){
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginRoomListViewActivity.this);
            builder.setTitle("Ошибка!")
                    .setMessage("Вы отключились!")
                    .setCancelable(false)
                    .setNegativeButton("Перезагрузить приложение",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent intent = new Intent(LoginRoomListViewActivity.this, MainActivity.class);
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
                        ActivityCompat.finishAffinity(LoginRoomListViewActivity.this);
                    }
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.rooms_menu, menu);

        MenuItem searchAction = menu.findItem(R.id.room_filter_action);
        SearchView searchView = (SearchView) searchAction.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(TextUtils.isEmpty(newText)){
                    adapter.filter("");
                    listView.clearTextFilter();
                }else{
                    adapter.filter(newText);
                }
                return true;
            }
        });

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }
    public void onBackPressed() {
        Intent intent = new Intent(this, JoinOrCreateRoomActivity.class);
        startActivity(intent);
    }
}
