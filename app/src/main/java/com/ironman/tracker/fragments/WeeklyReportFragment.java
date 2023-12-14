package com.ironman.tracker.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.ironman.tracker.R;
import com.ironman.tracker.adapters.TabViewPagerAdapter;
import com.ironman.tracker.utills.OnFragmentChangeListener;

import java.util.ArrayList;

public class WeeklyReportFragment extends Fragment {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TabViewPagerAdapter viewPagerAdapter;
    private ArrayList<Fragment> fragments;
    private String uid;
    private OnFragmentChangeListener onFragmentChangeListener;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.weekly_report_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager=view.findViewById(R.id.view_pager);
        tabLayout=view.findViewById(R.id.tab_layout);
        fragments=new ArrayList<>();
        fragments.add(new AppStatsFragment(uid));
        fragments.add(new CallStatsFragment(uid));
        fragments.add(new LocationStatsFragment(uid));
        viewPagerAdapter=new TabViewPagerAdapter(getChildFragmentManager(),fragments);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPagerAdapter.notifyDataSetChanged();
    }

    public WeeklyReportFragment(String uid, OnFragmentChangeListener onFragmentChangeListener) {
        this.uid = uid;
        this.onFragmentChangeListener = onFragmentChangeListener;
    }
}
