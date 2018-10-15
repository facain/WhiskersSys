package com.example.whiskersapp.petwhiskers.ViewHolder;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import com.example.whiskersapp.petwhiskers.ChatActivity;
import com.example.whiskersapp.petwhiskers.Model.User;
import com.example.whiskersapp.petwhiskers.Model.UserChat;
import com.example.whiskersapp.petwhiskers.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


public class MessageViewHolder extends RecyclerView.Adapter<MessageViewHolder.MessagePetViewHolder>{
    private Context context;
    private List<UserChat> msgList;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference dbUser = firebaseDatabase.getReference("user_account");

    public MessageViewHolder(Context ctx, List<UserChat> msgList){
        this.context = ctx;
        this.msgList = msgList;
    }

    @Override
    public MessagePetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardlayout_msg
                ,parent, false);
        return new MessageViewHolder.MessagePetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MessagePetViewHolder holder, int position) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final UserChat user = msgList.get(position);
        String id = null;

        holder.setMsgTime(user.getLast_time());
        holder.setMsgTxt(user.getLast_msg());

        if(user.getUser_two_id().equals(mAuth.getCurrentUser().getUid())){
            id = user.getUser_one_id();
        }else{
            id = user.getUser_two_id();
        }

        final String tempId = id;
        dbUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User test = null;
                User name = null;

                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    test = ds.getValue(User.class);

                    if(test.getId().equals(tempId)){
                        name = test;
                    }
                }

                if(name != null){
                    holder.setMsgName(name.getFname()+" "+name.getLname());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Feature not available!", Snackbar.LENGTH_SHORT).show();

                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("user_one_id", user.getUser_one_id());
                intent.putExtra("user_two_id", user.getUser_two_id());

                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }

    public class MessagePetViewHolder extends RecyclerView.ViewHolder{
        private TextView msgName, msgTxt, msgTime;
        private CardView cardView;

        public MessagePetViewHolder(View itemView) {
            super(itemView);

            msgName = itemView.findViewById(R.id.msg_user_name);
            msgTxt = itemView.findViewById(R.id.msg_message);
            msgTime = itemView.findViewById(R.id.msg_time);
            cardView = itemView.findViewById(R.id.cardview_msg);
        }

        public void setMsgName(String msg_name){
            msgName.setText(msg_name);
        }

        public void setMsgTxt(String msg_txt){
            msgTxt.setText(msg_txt);
        }

        public void setMsgTime(String msg_time){
            msgTime.setText("Sent "+msg_time);
        }
    }

}
