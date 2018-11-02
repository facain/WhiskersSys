package com.example.whiskersapp.petwhiskers;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.whiskersapp.petwhiskers.Model.Pet;
import com.example.whiskersapp.petwhiskers.ViewHolder.PetHomeViewHolder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {
    private Pet pet;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference dbPet;
    private int count;

    private PetHomeViewHolder homeAdapter;
    private TextView[] dots;
    private FirebaseAuth mAuth;
    private List<Pet> petList;
    private ViewPager viewPager;
    private LinearLayout dotsLayout;




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

        viewPager = view.findViewById(R.id.homeVP);
        dotsLayout =  view.findViewById(R.id.home_dots);
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
                count = ctr;
                addDotsIndicator(0,count);
                homeAdapter = new PetHomeViewHolder(getContext(), petList);
                viewPager.setAdapter(homeAdapter);
                viewPager.addOnPageChangeListener(viewListener);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


    }
    private void addDotsIndicator(int position, int size){
        dots = new TextView[size];
        dotsLayout.removeAllViews();
        for(int x = 0; x < dots.length; x++){
            dots[x] = new TextView(getContext());
            dots[x].setText(Html.fromHtml("&#8226;"));
            dots[x].setTextSize(35);
            dots[x].setTextColor(Color.parseColor("#cccccc"));
            dotsLayout.addView(dots[x]);
        }
        if(dots.length > 0){
            dots[position].setTextColor(Color.parseColor("#ffffff"));
        }
    }
    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position,count);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
