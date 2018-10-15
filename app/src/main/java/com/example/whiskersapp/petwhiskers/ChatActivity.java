package com.example.whiskersapp.petwhiskers;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whiskersapp.petwhiskers.Model.ChatMessage;
import com.example.whiskersapp.petwhiskers.Model.User;
import com.example.whiskersapp.petwhiskers.Model.UserChat;
import com.example.whiskersapp.petwhiskers.ViewHolder.ChatViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ChatActivity extends AppCompatActivity {

    private EditText message;
    private ImageView send;
    private TextView ownerName;
    private Toolbar toolbar;
    private User user;
    private User userOne;
    private User userTwo;
    private User curUser;
    private UserChat chat;
    private UserChat curChat;
    private User sendChat;
    private UserChat us;

    private String user_one_id;
    private String user_two_id;
    private String name_one;
    private String name_two;

    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference dbUserChat;
    private DatabaseReference dbChatMessage;
    private DatabaseReference dbUser;
    private FirebaseRecyclerAdapter<ChatMessage, ChatViewHolder> adapter;

    private RecyclerView recyclerview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        message = findViewById(R.id.chat_msg);
        send = findViewById(R.id.send_msg);
        ownerName = findViewById(R.id.chat_ownerName);
        recyclerview = findViewById(R.id.chat_list);
        send = findViewById(R.id.send_msg);
        toolbar = findViewById(R.id.toolbar_chat);
        toolbar.setNavigationIcon(R.drawable.ic_back_24dp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        user_one_id = getIntent().getStringExtra("user_one_id");
        user_two_id = getIntent().getStringExtra("user_two_id");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        dbUserChat = firebaseDatabase.getReference("user_chat");
        dbUser = firebaseDatabase.getReference("user_account");
        dbChatMessage = firebaseDatabase.getReference("chat_message");

        user = null;
        chat=null;
        curUser = null;
        curChat = null;
        sendChat=null;
        userOne = null;
        userTwo = null;

        String chatname = null;

        if(mAuth.getCurrentUser().getUid().equals(user_one_id)){
            chatname = user_two_id;
        }else{
            chatname = user_one_id;
        }

       final String temp_id = chatname;
        dbUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot ds: dataSnapshot.getChildren()) {
                        User temp = ds.getValue(User.class);

                        if (temp.getId().equals(temp_id) && temp.getBanStat().equals("0")) {
                            user = temp;
                        }
                    }

                    if(user != null){
                        ownerName.setText(user.getFname()+" "+user.getLname());

                        dbUserChat.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(DataSnapshot ds: dataSnapshot.getChildren()){
                                    UserChat userChat = ds.getValue(UserChat.class);

                                    if((userChat.getUser_one_id().equals(user_one_id) && userChat.getUser_two_id().equals(user_two_id))
                                            || (userChat.getUser_one_id().equals(user_two_id) && userChat.getUser_two_id().equals(user_one_id))){
                                        chat = ds.getValue(UserChat.class);
                                    }
                                }

                                if(chat == null){
                                    addUserChat(user_one_id, user_two_id);
                                }

                                displayList(chat.getId());
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

    }

    public void displayList(String id){
        final LinearLayoutManager lm = new LinearLayoutManager(getParent());
        lm.setStackFromEnd(true);
        recyclerview.setLayoutManager(lm);

        adapter = new FirebaseRecyclerAdapter<ChatMessage, ChatViewHolder>(
                ChatMessage.class,
                R.layout.cardlayout_chat,
                ChatViewHolder.class,
                dbChatMessage.orderByChild("userChat_id").equalTo(chat.getId())
        ) {
            @Override
            protected void populateViewHolder(ChatViewHolder viewHolder, ChatMessage model, int position) {
                Log.e("uidAuth",mAuth.getCurrentUser().getUid());
                Log.e("model",model.getMessageUser());
               if(mAuth.getCurrentUser().getUid().equals(model.getMessageUser())){
                   viewHolder.setName(model.getMessageName());
                   viewHolder.setMsg(model.getMessageText());
                   viewHolder.setTime(model.getMessageTime());
                   viewHolder.setNameColortoWhite();
                   viewHolder.setTimeColortoWhite();
                   viewHolder.chat.setTextColor(Color.rgb(255,255,255));
                   viewHolder.cardViewChat.setCardBackgroundColor(Color.rgb(51,149,191));
                   viewHolder.cardViewChat.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View view) {
                           Snackbar.make(getCurrentFocus(), "Feature not available", Snackbar.LENGTH_SHORT).show();
                       }
                   });

               }else {
                   viewHolder.setName(model.getMessageName());
                   viewHolder.setMsg(model.getMessageText());
                   viewHolder.setTime(model.getMessageTime());
                   viewHolder.cardViewChat.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View view) {
                           Snackbar.make(getCurrentFocus(), "Feature not available", Snackbar.LENGTH_SHORT).show();
                       }
                   });
               }
            }
        };

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int messageCount = adapter.getItemCount();
                int lastVisiblePosition = lm.findLastVisibleItemPosition();
                if(lastVisiblePosition==-1 || (positionStart>=(messageCount-1)&& lastVisiblePosition == (positionStart-1))){
                    recyclerview.smoothScrollToPosition(positionStart);
                }
            }
        });

        recyclerview.smoothScrollToPosition(adapter.getItemCount());
        adapter.notifyItemChanged(adapter.getItemCount());
        adapter.startListening();
        recyclerview.setAdapter(adapter);
    }

    public void sendMessage(){
        final String msg = message.getText().toString();
        Date tentime = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss a");
        final String time = df.format(tentime);
        final String id = dbChatMessage.push().getKey();

        if(!TextUtils.isEmpty(msg)){
            dbUserChat.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds:dataSnapshot.getChildren()){
                        UserChat test = ds.getValue(UserChat.class);

                        if ((test.getUser_one_id().equals(user_one_id) && test.getUser_two_id().equals(user_two_id))
                                || (test.getUser_one_id().equals(user_two_id) && test.getUser_two_id().equals(user_one_id))) {
                            curChat = test;
                        }
                    }

                    if(curChat != null){
                        String temp_id;

                        if(curChat.getUser_one_id().equals(mAuth.getCurrentUser().getUid())){
                            temp_id = curChat.getUser_one_id();
                        }else{
                            temp_id = curChat.getUser_two_id();
                        }

                        final String tmp = temp_id;
                        dbUser.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                for(DataSnapshot ds : dataSnapshot.getChildren()){
                                    User test = ds.getValue(User.class);

                                    if(test.getId().equals(tmp)){
                                        sendChat = test;
                                    }
                                }

                                if(sendChat != null && curChat != null){
                                    ChatMessage chat = new ChatMessage();

                                    chat.setId(id);
                                    chat.setMessageName(sendChat.getFname());
                                    chat.setMessageText(msg);
                                    chat.setMessageTime(time);
                                    chat.setMessageUser(sendChat.getId());
                                    chat.setUserChat_id(curChat.getId());

                                    curChat.setLast_msg(msg);
                                    curChat.setLast_time(time);

                                    if(curChat.getIsReadUserOne().equals(curChat.getId())){
                                        curChat.setIsReadUserTwo("yes");
                                        curChat.setIsReadUserOne("no");
                                    }else if(curChat.getIsReadUserTwo().equals(curChat.getId())){
                                        curChat.setIsReadUserOne("yes");
                                        curChat.setIsReadUserTwo("no");
                                    }


                                    dbUserChat.child(curChat.getId()).setValue(curChat);
                                    dbChatMessage.child(id).setValue(chat);
                                }else{
                                    Snackbar.make(getCurrentFocus(), "Message not sent.", Snackbar.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }else{
                        Snackbar.make(getCurrentFocus(), "Message not sent.", Snackbar.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }else{
            Snackbar.make(getCurrentFocus(), "Please input message.", Snackbar.LENGTH_SHORT).show();
        }


        message.setText("");
    }

    public void previousActivity(){
       this.finish();
    }

    public void addUserChat(final String user_one_id, String user_two_id){
        us = new UserChat();
        String id = dbUserChat.push().getKey();

        name_one = null;
        name_two = null;

        us.setId(id);
        us.setUser_one_id(user_one_id);
        us.setUser_two_id(user_two_id);
        us.setIsReadUserOne("no");
        us.setIsReadUserTwo("no");
        us.setLast_msg("");
        dbUserChat.child(id).setValue(us);
        chat = us;
    }

}
