package com.ironman.tracker.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.ironman.tracker.services.BackgroundTask;
import com.ironman.tracker.userManagement.UserManagementActivity;
import com.ironman.tracker.utills.Preference;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseCrashlytics.getInstance().setCustomKey("str_key", "hello");

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        // signed in
        if(currentUser!=null) {
            mainActivity();
        }
        // not signed in
        else {
            userManagementActivity();
        }

    }

    void userManagementActivity(){
        startActivity(new Intent(this, UserManagementActivity.class));
        finish();
    }

    void mainActivity(){
        startActivity(new Intent(this, MainActivity.class));
        Preference preference=new Preference(this);
        if(preference.isBackgroundTrackingEnabled()){
            startService(new Intent(this, BackgroundTask.class));
        }
        finish();
    }
}