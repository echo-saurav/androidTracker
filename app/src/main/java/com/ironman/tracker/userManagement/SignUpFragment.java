package com.ironman.tracker.userManagement;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ironman.tracker.activities.MainActivity;
import com.ironman.tracker.R;
import com.ironman.tracker.database.Firebase;
import com.ironman.tracker.utills.OnCreateUserListener;
import com.ironman.tracker.utills.OnFragmentChangeListener;

public class SignUpFragment extends Fragment {
    private EditText userNameEditText,emailEditTest, passwordEditText,passwordEdit2Text;
    private OnFragmentChangeListener onFragmentChangeListener;
    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.signup,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userNameEditText =view.findViewById(R.id.user_name);
        emailEditTest =view.findViewById(R.id.email);
        passwordEditText =view.findViewById(R.id.password);
        passwordEdit2Text =view.findViewById(R.id.password2);
        progressDialog=new ProgressDialog(getContext());
        progressDialog.setTitle("Loading...");

        view.findViewById(R.id.signUpButton).setOnClickListener(v -> {
            String userName= userNameEditText.getText().toString().trim();
            String email=emailEditTest.getText().toString().trim();
            String password=passwordEditText.getText().toString().trim();
            String password2=passwordEdit2Text.getText().toString().trim();

            // if any field is empty
            if(!email.isEmpty() && !password.isEmpty() && !password2.isEmpty() && !userName.isEmpty() ){
                // if password matches
                if(password.equals(password2)){
                    signUp(email,password,userName);
                }else {
                    Toast.makeText(getContext(),"Password didn't match",Toast.LENGTH_LONG).show();
                }
            }
            else {
                Toast.makeText(getContext(),"Please enter every field",Toast.LENGTH_LONG).show();
            }

        });

    }

    void signUp(String email,String password,String userName){
        progressDialog.show();
        Firebase.createNewUser(email, password, userName,new OnCreateUserListener() {
            @Override
            public void onComplete() {
                progressDialog.dismiss();
                signUpSuccess();
            }

            @Override
            public void onError(String error) {
                progressDialog.dismiss();
                Toast.makeText(getContext(),"Sign up failed "+ error,Toast.LENGTH_LONG).show();
            }
        });
    }

    void signUpSuccess(){
        if(getActivity()!=null){
            startActivity(new Intent(getContext(), MainActivity.class));
            getActivity().finish();
        }
    }

    public SignUpFragment(OnFragmentChangeListener onFragmentChangeListener) {
        this.onFragmentChangeListener = onFragmentChangeListener;
    }
}
