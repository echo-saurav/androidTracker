package com.ironman.tracker.userManagement;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.ironman.tracker.R;
import com.ironman.tracker.utills.OnFragmentChangeListener;

public class ResetPasswordFragment extends Fragment {
    private EditText emailEditText;
    private OnFragmentChangeListener onFragmentChangeListener;
    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.resetpassword,container,false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        emailEditText=view.findViewById(R.id.email);
        progressDialog=new ProgressDialog(getContext());
        progressDialog.setTitle("Loading...");

        view.findViewById(R.id.resetPassButton).setOnClickListener(v -> {
            String email=emailEditText.getText().toString();
            if(!email.isEmpty()){
                progressDialog.show();
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            progressDialog.dismiss();
                            if(task.isSuccessful()){
                                Toast.makeText(getContext(),"Reset password email have sent , please check your email to reset password",Toast.LENGTH_LONG).show();
                            }
                            if(task.getException()!=null){
                                Toast.makeText(getContext(),"Password reset failed "+ task.getException().getMessage(),Toast.LENGTH_LONG).show();
                            }
                        });


            }else {
                Toast.makeText(getContext(),"Please enter email",Toast.LENGTH_LONG).show();
            }


        });
    }


    public ResetPasswordFragment(OnFragmentChangeListener onFragmentChangeListener) {
        this.onFragmentChangeListener = onFragmentChangeListener;
    }
}
