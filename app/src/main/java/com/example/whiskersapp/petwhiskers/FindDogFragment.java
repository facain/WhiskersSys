package com.example.whiskersapp.petwhiskers;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.whiskersapp.petwhiskers.Model.Pet;
import com.example.whiskersapp.petwhiskers.Model.User;
import com.example.whiskersapp.petwhiskers.ViewHolder.PetViewHolder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class  FindDogFragment extends Fragment implements SearchView.OnQueryTextListener{

    private RecyclerView recyclerview;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference dbRef;
    private DatabaseReference dbAcc;
    private List<Pet> petList;
    private FirebaseAuth mAuth;
    private PetViewHolder bookmarkAdapter;

    MenuItem search;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_find_dog, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerview = view.findViewById(R.id.FindDogRV);
        petList = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        dbRef = firebaseDatabase.getReference("pet");
        dbAcc = firebaseDatabase.getReference("user_account");

        dbAcc.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user;
                for (DataSnapshot dsr : dataSnapshot.getChildren()) {
                    user = dsr.getValue(User.class);

                    if (user.getBanStat().equals("0")) {
                        dbRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                petList.clear();
                                String ownerId = mAuth.getCurrentUser().getUid();
                                Pet test;

                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    test = ds.getValue(Pet.class);
                                    if (!test.getOwner_id().equals(ownerId) && test.getCategory().equals("Dog") &&
                                            test.getIsAdopt().equals("no") && test.getVerStat().equals("1")) {
                                        petList.add(test);
                                    }
                                }

                                LinearLayoutManager llm = new LinearLayoutManager(getContext());
                                llm.setOrientation(LinearLayoutManager.VERTICAL);
                                recyclerview.setLayoutManager(llm);

                                bookmarkAdapter = new PetViewHolder(getContext(), petList);
                                recyclerview.setAdapter(bookmarkAdapter);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(getContext(), "Error in retrieving data!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
                @Override
                public void onCancelled (@NonNull DatabaseError databaseError){


                }

        });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        search = menu.findItem(R.id.searchBar).setVisible(true);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        searchView.setOnQueryTextListener(FindDogFragment.this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        newText = newText.toLowerCase();
        ArrayList<Pet> newList = new ArrayList<>();

        for(Pet pet: petList){
            String name = pet.getBreed().toLowerCase();

            if(name.contains(newText)){
                newList.add(pet);
            }

        }

        bookmarkAdapter.setFilter(newList);
        return false;
    }
}
