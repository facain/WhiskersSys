package com.example.whiskersapp.petwhiskers;

import android.annotation.SuppressLint;
import android.graphics.Color;

import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


public class FindPetFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find_pet, container, false);
        return view;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        android.support.v7.widget.Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Find Pet");

        tabLayout = view.findViewById(R.id.findpet_tablayout);
        viewPager = view.findViewById(R.id.findpet_viewpager);

        PetViewPagerAdapter petAdapter = new PetViewPagerAdapter(getChildFragmentManager());

        petAdapter.getFragment(new FindCatFragment(), "Cat");
        petAdapter.getFragment(new FindDogFragment(), "Dog");

        viewPager.setAdapter(petAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem search = menu.findItem(R.id.searchBar).setVisible(true);
        search.setTitle("Type Breed...");
    }
}
