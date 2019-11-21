package com.example.chatapp.listViewStuff;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.chatapp.R;

import java.util.List;

public class ChatAdapter extends ArrayAdapter<ChatBubble> {

    private Activity activity;
    private List<ChatBubble> messages;

    public ChatAdapter(Activity context, int resource, List<ChatBubble> objects) {
        super(context, resource, objects);
        this.activity = context;
        this.messages = objects;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder = null;
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        int layoutResource = 0;
        ChatBubble chatBubble = getItem(position);
        if(chatBubble.isMyMessage()){
            layoutResource = R.layout.my_buble;
        }else{
            layoutResource = R.layout.not_my_bubble;
        }
        if(convertView != null){
            holder = (ViewHolder) convertView.getTag();
        }else{
            convertView = inflater.inflate(layoutResource, parent, false);
            holder = new ViewHolder((TextView) convertView.findViewById(R.id.msg), (TextView)convertView.findViewById(R.id.nickname), (TextView)convertView.findViewById(R.id.time));
            convertView.setTag(holder);
        }
        holder.nickname.setText(chatBubble.getNickname());
        holder.msg.setText(chatBubble.getMsg());
        holder.date.setText(chatBubble.getDate());
        return convertView;
    }
    @Override
    public int getViewTypeCount() {
        // return the total number of view types. this value should never change
        // at runtime. Value 2 is returned because of left and right views.
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        ChatBubble chatBubble = getItem(position);
        if(chatBubble.isMyMessage()){
            return 0;
        }else{
            return 1;
        }
    }

    private class ViewHolder {
        public TextView msg;
        public TextView nickname;
        public TextView date;

        public ViewHolder(TextView msg, TextView nickname, TextView date) {
            this.msg = msg;
            this.nickname = nickname;
            this.date = date;
        }

        public TextView getMsg() {
            return msg;
        }

        public TextView getNickname() {
            return nickname;
        }

        public TextView getDate() {
            return date;
        }
    }
}
