package com.example.whiskersapp.petwhiskers;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.whiskersapp.petwhiskers.ViewHolder.PetListViewHolder;


public class PetFragment extends Fragment{
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pet, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        android.support.v7.widget.Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Pet Stop");
        tabLayout = view.findViewById(R.id.pet_tablayout);
        viewPager = view.findViewById(R.id.pet_viewpager);

        PetViewPagerAdapter petAdapter = new PetViewPagerAdapter(getChildFragmentManager());

        petAdapter.getFragment(new PetListFragment(), "My Entry");
        petAdapter.getFragment(new PetBookmarkFragment(), "Bookmark Entry");

        viewPager.setAdapter(petAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}
