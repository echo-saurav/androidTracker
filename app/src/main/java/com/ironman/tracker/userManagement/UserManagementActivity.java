package com.ironman.tracker.userManagement;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.ironman.tracker.R;
import com.ironman.tracker.utills.OnFragmentChangeListener;

public class UserManagementActivity extends AppCompatActivity implements OnFragmentChangeListener {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_manage_layout);

        Fragment loginFragment,resetFragment,signUpFragment;

        loginFragment=new LoginFragment(this);
        resetFragment=new ResetPasswordFragment(this);
        signUpFragment=new SignUpFragment(this);

        getSupportFragmentManager().beginTransaction().add(R.id.parentFrame, loginFragment).commit();
    }

    @Override
    public void onFragmentChange(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.parentFrame, fragment).addToBackStack(null).commit();
    }
}
