package com.example.whiskersapp.petwhiskers.ViewHolder;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.whiskersapp.petwhiskers.R;

public class ChatViewHolder extends RecyclerView.ViewHolder {
    private TextView name, msg, time;
    public CardView cardViewChat;
    public TextView chat;

    public ChatViewHolder(View itemView) {
        super(itemView);

        name = itemView.findViewById(R.id.chat_user_name);
        msg = itemView.findViewById(R.id.chat_message);
        time = itemView.findViewById(R.id.chat_time);
        cardViewChat = itemView.findViewById(R.id.cardview_chat);
        chat = itemView.findViewById(R.id.chat_message);
    }
    public  void setNameColortoWhite(){name.setTextColor(Color.rgb(255,255,255));}
    public void setTimeColortoWhite(){time.setTextColor(Color.rgb(255,255,255));}
    public void setName(String Msgname) {
        name.setText(Msgname);
    }

    public void setMsg(String Txtmsg) {
        msg.setText(Txtmsg);
    }

    public  void setTime(String Msgtime) {
        time.setText("Sent "+Msgtime);
    }

}
