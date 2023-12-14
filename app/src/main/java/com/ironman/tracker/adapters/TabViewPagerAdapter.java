package com.ironman.tracker.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class TabViewPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> viewPagerValueHolderArrayList;
    private String[] tabNames={"App Usage","Call Log","Location History"};


    public TabViewPagerAdapter(@NonNull FragmentManager fm, ArrayList<Fragment> viewPagerValueHolderArrayList) {
        super(fm);
        this.viewPagerValueHolderArrayList = viewPagerValueHolderArrayList;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return viewPagerValueHolderArrayList.get(position);
    }

    @Override
    public int getCount() {
        return viewPagerValueHolderArrayList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabNames[position];
    }
}
