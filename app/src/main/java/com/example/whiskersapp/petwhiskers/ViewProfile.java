package com.example.whiskersapp.petwhiskers;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whiskersapp.petwhiskers.Model.LikeDislike;
import com.example.whiskersapp.petwhiskers.Model.Pet;
import com.example.whiskersapp.petwhiskers.Model.User;

import com.example.whiskersapp.petwhiskers.ViewHolder.ProfilePetViewHolder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewProfile extends AppCompatActivity {
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference pet_entry;
    DatabaseReference dblikeDislike;
    ImageButton likedButton, likeButton, dislikedButton, dislikeButton;
    TextView userName, userEmail, userLikes, userDislikes,userNumOfEntries;
    String buttonStatus;
    RecyclerView recyclerView;
    private List<Pet> petList;
    Pet pet;
    String id="";
    int ctrLike=0, ctrDislike=0;
    FirebaseAuth firebaseAuth;
    Toolbar toolbar;
    ProfilePetViewHolder profileAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        likedButton = findViewById(R.id.button_liked);
        likeButton = findViewById(R.id.button_like);
        dislikedButton = findViewById(R.id.button_disliked);
        dislikeButton = findViewById(R.id.button_dislike);
        userName = findViewById(R.id.text_profile_name);
        userEmail = findViewById(R.id.text_profile_email);
        userLikes = findViewById(R.id.ctr_likes);
        userDislikes = findViewById(R.id.ctr_dislikes);
        userNumOfEntries = findViewById(R.id.ctr_pets);
        recyclerView = findViewById(R.id.UserProfilePetRV);
        toolbar = findViewById(R.id.toolbar_view_profile);
        toolbar.setNavigationIcon(R.drawable.ic_back_24dp);
        pet_entry = firebaseDatabase.getReference("pet");
        /*LinearLayoutManager linear = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(linear);*/
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        pet=null;
        if(getIntent()!=null){
            id = getIntent().getStringExtra("id");
            if(!id.isEmpty()){
                buttonStatus="";
                getUserDetails(id);
                getCtrLikeDislike(id);
                getCtrPetEntries(id);

                likeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        buttonStatus = "Like";
                        setActionLikeDislike("add");
                    }
                });
                likedButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        buttonStatus = "Default";
                        setActionLikeDislike("remove");

                    }
                });
                dislikeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        buttonStatus = "Dislike";
                        setActionLikeDislike("add");
                    }
                });
                dislikedButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        buttonStatus = "Default";
                        setActionLikeDislike("remove");
                    }
                });

                petList = new ArrayList<>();
                pet_entry.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        petList.clear();
                        Pet test;

                        for(DataSnapshot ds: dataSnapshot.getChildren()){
                            test = ds.getValue(Pet.class);
                            if(test.getOwner_id().equals(id)&&
                                    test.getIsAdopt().equals("no") && test.getVerStat().equals("1")){
                                petList.add(test);
                            }

                        }

                        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
                        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
                        recyclerView.setLayoutManager(llm);

                        profileAdapter = new ProfilePetViewHolder(getApplicationContext(),petList);

                        recyclerView.setAdapter(profileAdapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        }
    }

    private void getCtrPetEntries(final String id) {

        pet_entry.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int ctr =0;
                Pet test;
                for(DataSnapshot children: dataSnapshot.getChildren()) {
                    test = children.getValue(Pet.class);
                    if(test.getOwner_id().equals(id)&& test.getIsAdopt().equals("no") && test.getVerStat().equals("1")){
                        ctr++;
                    }
                }
                userNumOfEntries.setText(String.valueOf(ctr));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void getCtrLikeDislike(final String id) {
        dblikeDislike = firebaseDatabase.getReference("like_system");
        dblikeDislike.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                LikeDislike test,temp=null;
                ctrLike = 0;
                ctrDislike = 0;

                for(DataSnapshot children: dataSnapshot.getChildren()) {
                    test = children.getValue(LikeDislike.class);
                    if (test.getUser_profile_id().equals(id) && test.getRateSystem().equals("Like")) {
                        ctrLike++;
                    }else if(test.getUser_profile_id().equals(id) && test.getRateSystem().equals("Dislike")){
                        ctrDislike++;
                    }
                    if(test.getUser_profile_id().equals(id)&&test.getLiker().equals(firebaseAuth.getCurrentUser().getUid())){
                        temp = test;
                    }

                }
                if(temp!=null){
                    if(temp.getRateSystem().equals("Like")){
                        buttonStatus = "Like";
                    }else if(temp.getRateSystem().equals("Dislike")){
                        buttonStatus = "Dislike";
                    }
                }else{
                    buttonStatus = "Default";
                }
                setButtonStatus(buttonStatus);
                userLikes.setText(String.valueOf(ctrLike));
                userDislikes.setText(String.valueOf(ctrDislike));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getUserDetails(String id) {
        DatabaseReference user = firebaseDatabase.getReference("user_account");

        user.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                userName.setText(user.getFname() +" "+user.getLname());
                userEmail.setText(user.getEmail());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }
    public void setButtonStatus(String buttonStatus){
        if(buttonStatus.equals("Like")) {
            likedButton.setVisibility(View.VISIBLE);
            likeButton.setVisibility(View.INVISIBLE);
            dislikeButton.setVisibility(View.VISIBLE);
            dislikedButton.setVisibility(View.INVISIBLE);
        }else if(buttonStatus.equals("Default")){
            likeButton.setVisibility(View.VISIBLE);
            likedButton.setVisibility(View.INVISIBLE);
            dislikeButton.setVisibility(View.VISIBLE);
            dislikedButton.setVisibility(View.INVISIBLE);
        }else if(buttonStatus.equals("Dislike")){
            dislikedButton.setVisibility(View.VISIBLE);
            dislikeButton.setVisibility(View.INVISIBLE);
            likeButton.setVisibility(View.VISIBLE);
            likedButton.setVisibility(View.INVISIBLE);
        }

    }

    private void setActionLikeDislike(final String action) {
        final DatabaseReference dbLikeDislike = firebaseDatabase.getReference("like_system");
        final String user_profile_id = getIntent().getStringExtra("id");
        if(user_profile_id!=null){
            dbLikeDislike.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    LikeDislike temp = null;

                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        LikeDislike test = ds.getValue(LikeDislike.class);

                        if(test.getUser_profile_id().equals(user_profile_id)&&test.getLiker().equals(firebaseAuth.getCurrentUser().getUid())){
                            temp=test;
                            Log.e("Testsearch",temp.getId());
                        }
                    }
                    String rate_status="";
                    if(temp != null){
                        if(action.equals("add")){
                            if(!buttonStatus.equals(temp.getRateSystem())){
                                temp.setRateSystem(buttonStatus);
                                dbLikeDislike.child(temp.getId()).setValue(temp);
                            }


                        }else if(action.equals("remove")&&buttonStatus.equals("Default")){
                            dbLikeDislike.child(temp.getId()).removeValue();
                            // setButtonStatus(buttonStatus);
                            Log.e("Im here","delete");

                        }
                        Log.e("Im here","hi");
                    }else{
                        if(action.equals("add")&&!buttonStatus.equals("Default")){
                            LikeDislike likeDislike = new LikeDislike();
                            if(buttonStatus.equals("Like")){
                                rate_status = "Like";
                            }else{
                                rate_status="Dislike";
                            }
                            //setButtonStatus(buttonStatus);
                            String id = dbLikeDislike.push().getKey();
                            Log.e("Add:",id);
                            likeDislike.setId(id);
                            likeDislike.setLiker(firebaseAuth.getCurrentUser().getUid());
                            likeDislike.setRateSystem(rate_status);
                            likeDislike.setUser_profile_id(user_profile_id);
                            dbLikeDislike.child(id).setValue(likeDislike);
                            Log.e("Huli ka","yo");


                        }
                        Log.e("Im here","yo");

                    }
                    Log.e("ButtonStatus",buttonStatus);
                    setButtonStatus(buttonStatus);


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), "Error in DB!", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(getApplicationContext(), "ID not found!", Toast.LENGTH_SHORT).show();
        }
    }
    public void messageOwner(View view){
        String user_id="";
        user_id = getIntent().getStringExtra("id");
        if(!user_id.isEmpty()){
            Intent intent = new Intent(ViewProfile.this, ChatActivity.class);

            intent.putExtra("user_one_id", firebaseAuth.getCurrentUser().getUid());
            intent.putExtra("user_two_id", id);

            startActivity(intent);
        }

    }



}
