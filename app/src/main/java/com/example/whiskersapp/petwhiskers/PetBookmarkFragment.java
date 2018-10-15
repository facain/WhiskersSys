package com.example.whiskersapp.petwhiskers;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.whiskersapp.petwhiskers.Model.Bookmark;
import com.example.whiskersapp.petwhiskers.Model.Pet;
import com.example.whiskersapp.petwhiskers.ViewHolder.BookmarksViewHolder;
import com.example.whiskersapp.petwhiskers.ViewHolder.PetListViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class PetBookmarkFragment extends Fragment {
    private RecyclerView recyclerview;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference table_pet_entry;
    private FirebaseRecyclerAdapter<Bookmark, PetListViewHolder> adapter;
    private FirebaseAuth mAuth;

    public PetBookmarkFragment(){}
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pet_bookmark, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerview = view.findViewById(R.id.BookMarkPetRV);
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        firebaseDatabase = FirebaseDatabase.getInstance();
        table_pet_entry = firebaseDatabase.getReference("bookmark");
        mAuth = FirebaseAuth.getInstance();
        adapter = new FirebaseRecyclerAdapter<Bookmark, PetListViewHolder>(
                Bookmark.class,
                R.layout.card_layout,
                PetListViewHolder.class,
                table_pet_entry.orderByChild("bookmark_user_id").equalTo(mAuth.getCurrentUser().getUid())
        ) {
            @Override
            protected void populateViewHolder(PetListViewHolder viewHolder, final Bookmark model, final int position) {
                viewHolder.setPetName(model.getPet_name());
                viewHolder.setPetBreed(model.getBreed());
                viewHolder.setPetGender(model.getGender());
                viewHolder.setPetStatus(model.getStatus());
                viewHolder.setPetImage(getContext(),model.getImgUrl());
                final Bookmark petInfo = model;
                viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), PetDetails.class);
                        intent.putExtra("id",model.getPet_id());
                        intent.putExtra("owner_id", model.getOwner_id());
                        startActivity(intent);
                    }
                });
            }
        };
        adapter.startListening();
        recyclerview.setAdapter(adapter);
    }
}
