package com.example.chatapp.listViewStuff;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapp.ChatActivitry;
import com.example.chatapp.Connection;
import com.example.chatapp.Container;
import com.example.chatapp.ErrorActivity;
import com.example.chatapp.LoginRoomActivity;
import com.example.chatapp.R;
import com.example.chatapp.HardMessage;
import com.example.chatapp.MessageType;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListViewAdapter extends BaseAdapter {

    Context mContext;
    LayoutInflater inflater;
    List<Model> modelList;
    ArrayList<Model> arrayList;

    public ListViewAdapter(Context mContext, List<Model> models) {
        this.mContext = mContext;
        this.modelList = models;
        inflater = LayoutInflater.from(mContext);
        this.arrayList = new ArrayList<>();
        this.arrayList.addAll(models);
    }

    public class ViewHolder{
        TextView mTitleTv, mDescriptionTv;
        ImageView mLockIconTv;
    }

    @Override
    public int getCount() {
        return modelList.size();
    }

    @Override
    public Object getItem(int position) {
        return modelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if(view == null){
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.row, null);
            holder.mTitleTv = view.findViewById(R.id.roomName);
            holder.mDescriptionTv = view.findViewById(R.id.roomDescription);
            holder.mLockIconTv = view.findViewById(R.id.lockIcon);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        holder.mTitleTv.setText(modelList.get(position).getTitle());
        holder.mDescriptionTv.setText(modelList.get(position).getDesc());
        holder.mLockIconTv.setImageResource(modelList.get(position).getImage());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modelList.get(position).getImage() == R.drawable.unlock){
                    try {
                        Connection connection = Connection.getInstance();
                        HardMessage hmsg = new HardMessage();
                        hmsg.setType(MessageType.HARD_MESSAGE_WITH_ARRAY_OF_LOGIN_ROOM);
                        hmsg.setStuff(new String[]{modelList.get(position).getTitle(), ""});
                        connection.send(hmsg);
                        while(true){
                            HardMessage msg = (HardMessage) connection.receive();
                            if (msg.getType() == MessageType.CHECK_ROOM_LOGIN_EXISTS) {
                                Intent intent = new Intent(mContext, ChatActivitry.class);
                                intent.putExtra("RoomName", msg.getData());
                                Container.setRoom_login(modelList.get(position).getTitle());
                                mContext.startActivity(intent);
                                break;
                            } else if (msg.getType() == MessageType.CHECK_ROOM_LOGIN_DOESNT_EXIST) {
                                Toast.makeText(mContext, "Непредвиденная ошибка, попробуйте зайти в другую комнату", Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }
                    }catch (Exception e){
                        Intent intent = new Intent(mContext, ErrorActivity.class);
                        intent.putExtra("Error", e);
                        mContext.startActivity(intent);
                    }
                }else if(modelList.get(position).getImage() == R.drawable.lock){
                    Intent intent = new Intent(mContext, LoginRoomActivity.class);
                    intent.putExtra("RoomName", modelList.get(position).getTitle());
                    mContext.startActivity(intent);
                }
            }
        });
        return view;
    }
    public void filter(String s){
        s = s.toLowerCase(Locale.getDefault());
        modelList.clear();
        if(s.length() == 0){
            modelList.addAll(arrayList);
        }else{
            for(Model model : arrayList){
                if(model.getTitle().toLowerCase(Locale.getDefault()).contains(s)){
                    modelList.add(model);
                }
            }
        }
        notifyDataSetChanged();
    }
}
