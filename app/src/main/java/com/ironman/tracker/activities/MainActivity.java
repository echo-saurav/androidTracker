package com.ironman.tracker.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ironman.tracker.R;
import com.ironman.tracker.fragments.HomeFragment;
import com.ironman.tracker.fragments.NotificationFragment;
import com.ironman.tracker.fragments.OtherProfileFragment;
import com.ironman.tracker.fragments.ProfileFragment;
import com.ironman.tracker.fragments.SettingFragment;
import com.ironman.tracker.utills.OnFragmentChangeListener;

public class MainActivity extends AppCompatActivity implements OnFragmentChangeListener, BottomNavigationView.OnNavigationItemSelectedListener {
    private Fragment home, profile,notification,setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        home=new HomeFragment(this);
        profile =new ProfileFragment(this);
        notification=new NotificationFragment(this);
        setting=new SettingFragment(this);

        getSupportFragmentManager().beginTransaction().add(R.id.parentFrame,home).commit();


    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.home:
                onFragmentChange(home);
                return true;
            case R.id.profile:
                onFragmentChange(profile);
                return true;
            case R.id.notification:
                onFragmentChange(notification);
                return true;
            case R.id.setting:
                onFragmentChange(setting);
                return true;
        }
        return false;
    }

    @Override
    public void onFragmentChange(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.parentFrame,fragment).addToBackStack(null).commit();

    }

}