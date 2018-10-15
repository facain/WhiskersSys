package com.example.whiskersapp.petwhiskers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.CardView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.whiskersapp.petwhiskers.Model.Pet;
import com.example.whiskersapp.petwhiskers.ViewHolder.PetHomeViewHolder;
import com.example.whiskersapp.petwhiskers.ViewHolder.PetListViewHolder;
import com.example.whiskersapp.petwhiskers.ViewHolder.PetViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {
    private Pet pet;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference dbPet;
    private int ctr;

    private PetHomeViewHolder homeAdapter;
    private ImageView imgPet;
    private TextView petname, petbreed, petstatus;
    private CardView cardView;
    private Button messageBtn;
    private FirebaseAuth mAuth;
    private List<Pet> petList;
    private RecyclerView recyclerview;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        android.support.v7.widget.Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        pet = null;

        firebaseDatabase = FirebaseDatabase.getInstance();
        dbPet = firebaseDatabase.getReference("pet");
        mAuth = FirebaseAuth.getInstance();

        recyclerview = view.findViewById(R.id.homeRV);
        LinearLayoutManager linear = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        recyclerview.setLayoutManager(linear);
        petList = new ArrayList<>();
        dbPet.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                petList.clear();
                String ownerId = mAuth.getCurrentUser().getUid();
                Pet test;
                int ctr = 0;

                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    test = ds.getValue(Pet.class);
                    if(ctr <= 2 && !test.getOwner_id().equals(ownerId)&&
                            test.getIsAdopt().equals("no") && test.getVerStat().equals("1")){
                        petList.add(test);
                        ctr++;
                    }

                }

                LinearLayoutManager llm = new LinearLayoutManager(getContext());
                llm.setOrientation(LinearLayoutManager.HORIZONTAL);
                recyclerview.setLayoutManager(llm);

                homeAdapter = new PetHomeViewHolder(getContext(), petList);
                recyclerview.setAdapter(homeAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
             }
        });


    }
}
