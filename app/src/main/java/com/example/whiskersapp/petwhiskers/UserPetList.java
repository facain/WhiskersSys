package com.example.whiskersapp.petwhiskers;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.whiskersapp.petwhiskers.Model.Pet;
import com.example.whiskersapp.petwhiskers.Model.User;
import com.example.whiskersapp.petwhiskers.ViewHolder.PetListViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserPetList extends AppCompatActivity {

    private RecyclerView recyclerview;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference table_pet_entry;
    private DatabaseReference table_pet_user;
    private FirebaseRecyclerAdapter<Pet, PetListViewHolder> adapter;
    private FirebaseAuth mAuth;
    String id = "";
    String distance = "";
    Toolbar toolbar;
    TextView petOwnerName;
    TextView kmDistance;
    ImageView messageOwner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_pet_list);
        if(getIntent()!=null){
            id = getIntent().getStringExtra("id");
            distance = getIntent().getStringExtra("distance");
            if(!id.isEmpty()&&!distance.isEmpty()){
                toolbar = (Toolbar) findViewById(R.id.toolbar_user_pet);
                toolbar.setNavigationIcon(R.drawable.ic_back_24dp);
                setSupportActionBar(toolbar);
                getSupportActionBar().setDisplayShowTitleEnabled(false);
                petOwnerName = findViewById(R.id.text_profile_user);
                kmDistance = findViewById(R.id.text_profile_location);
                messageOwner = findViewById(R.id.img_message_profile);
                kmDistance.setText(distance+"km away");

                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });


                recyclerview = findViewById(R.id.UserPetListRV);
                recyclerview.setLayoutManager(new LinearLayoutManager(this));
                firebaseDatabase = FirebaseDatabase.getInstance();
                table_pet_entry = firebaseDatabase.getReference("pet");
                table_pet_user = firebaseDatabase.getReference("user_account");


                mAuth = FirebaseAuth.getInstance();
                getPetOwnerDetails(id);
                messageOwner.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                        intent.putExtra("user_one_id", mAuth.getCurrentUser().getUid());
                        intent.putExtra("user_two_id", id);
                        startActivity(intent);
                    }
                });
                adapter = new FirebaseRecyclerAdapter<Pet, PetListViewHolder>(
                        Pet.class,
                        R.layout.card_layout,
                        PetListViewHolder.class,
                        table_pet_entry.orderByChild("owner_id").equalTo(id)
                ) {
                    @Override
                    protected void populateViewHolder(PetListViewHolder viewHolder, Pet model, final int position) {
                        if(model.getIsAdopt().equals("no") && model.getVerStat().equals("1")){
                            viewHolder.setPetName(model.getPet_name());
                            viewHolder.setPetBreed(model.getBreed());
                            viewHolder.setPetGender(model.getGender());
                            viewHolder.setPetStatus(model.getStatus());
                            viewHolder.setPetImage(getApplicationContext(),model.getImgUrl());
                            final Pet petInfo = model;
                            viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(UserPetList.this, PetDetails.class);
                                    intent.putExtra("id",adapter.getRef(position).getKey());
                                    intent.putExtra("owner_id", petInfo.getOwner_id());
                                    startActivity(intent);
                                }
                            });
                        }else{
                            viewHolder.cardView.setVisibility(View.GONE);
                            viewHolder.cardView.setLayoutParams(new RecyclerView.LayoutParams(0,0));
                        }
                    }
                };
                adapter.startListening();
                recyclerview.setAdapter(adapter);
            }
        }

    }

    private void  getPetOwnerDetails(String id) {
        table_pet_user.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                petOwnerName.setText(user.getFname() +" "+user.getLname());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
