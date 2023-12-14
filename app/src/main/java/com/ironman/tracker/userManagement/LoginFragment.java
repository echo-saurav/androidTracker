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

import com.google.firebase.auth.FirebaseAuth;
import com.ironman.tracker.activities.MainActivity;
import com.ironman.tracker.R;
import com.ironman.tracker.utills.OnFragmentChangeListener;

public class LoginFragment extends Fragment {
    private EditText emailEditTest, passwordEditText;
    private OnFragmentChangeListener onFragmentChangeListener;
    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.login,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        emailEditTest =view.findViewById(R.id.email);
        passwordEditText =view.findViewById(R.id.password);
        progressDialog=new ProgressDialog(getContext());
        progressDialog.setTitle("Loading...");

        view.findViewById(R.id.reset_pass).setOnClickListener(v -> {
            onFragmentChangeListener.onFragmentChange(new ResetPasswordFragment(onFragmentChangeListener));
        });

        view.findViewById(R.id.create_account_button).setOnClickListener(v -> {
            onFragmentChangeListener.onFragmentChange(new SignUpFragment(onFragmentChangeListener));
        });


        view.findViewById(R.id.loginButton).setOnClickListener(v -> {
            String email=emailEditTest.getText().toString().trim();
            String password=passwordEditText.getText().toString().trim();

            if(!email.isEmpty() && !password.isEmpty() ){
                login(email,password);
            }else {
                Toast.makeText(getContext(),"Please enter email and password",Toast.LENGTH_LONG).show();
            }
        });
    }

    void login(String email,String password){
        progressDialog.show();
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if(task.isSuccessful()){
                       loginSuccess();
                    }
                    if(task.getException()!=null){
                        Toast.makeText(getContext(),"Login failed "+ task.getException().getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
    }

    void loginSuccess(){
        if(getActivity()!=null){
            startActivity(new Intent(getContext(), MainActivity.class));
            getActivity().finish();
        }
    }

    public LoginFragment(OnFragmentChangeListener onFragmentChangeListener) {
        this.onFragmentChangeListener = onFragmentChangeListener;
    }
}
