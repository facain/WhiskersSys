package com.example.whiskersapp.petwhiskers;

import android.app.PendingIntent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.whiskersapp.petwhiskers.Model.Pet;
import com.example.whiskersapp.petwhiskers.Model.UserChat;
import com.example.whiskersapp.petwhiskers.ViewHolder.MessageViewHolder;
import com.example.whiskersapp.petwhiskers.ViewHolder.PetViewHolder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class MessageFragment extends Fragment {

    private RecyclerView recyclerview;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference dbRef;
    private List<UserChat> msgList;
    private FirebaseAuth mAuth;
    private MessageViewHolder messageAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_message, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FloatingActionButton PetEntryFab = getActivity().findViewById(R.id.cpefab);
        PetEntryFab.hide();
        android.support.v7.widget.Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Message");
        recyclerview = view.findViewById(R.id.MessageRV);
        msgList = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        dbRef = firebaseDatabase.getReference("user_chat");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                msgList.clear();
                String id = mAuth.getCurrentUser().getUid();
                UserChat test;

                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    test = ds.getValue(UserChat.class);
                    if(test.getLast_msg().equals("")){

                    }
                    else if(test.getUser_one_id().equals(id) || test.getUser_two_id().equals(id)){
                        msgList.add(test);
                    }
                }
                if(msgList == null){

                }else {
                    Collections.reverse(msgList);

                    LinearLayoutManager llm = new LinearLayoutManager(getContext());
                    llm.setOrientation(LinearLayoutManager.VERTICAL);
                    llm.setReverseLayout(true);
                    llm.setStackFromEnd(true);

                    recyclerview.setLayoutManager(llm);

                    messageAdapter = new MessageViewHolder(getContext(), msgList);
                    recyclerview.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
