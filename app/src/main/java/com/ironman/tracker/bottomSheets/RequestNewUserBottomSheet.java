package com.ironman.tracker.bottomSheets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.ironman.tracker.R;
import com.ironman.tracker.utills.OnMyCompleteListener;

import static com.ironman.tracker.database.Firebase.sendRequestCode;

public class RequestNewUserBottomSheet extends BottomSheetDialogFragment {
    private EditText codeEditText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.request_new_user_bottom_sheet, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        codeEditText=view.findViewById(R.id.code);

        view.findViewById(R.id.request_code_button).setOnClickListener(v -> {
            String code=codeEditText.getText().toString().trim();
            if(!code.isEmpty()){

                sendRequestCode(code, new OnMyCompleteListener() {
                    @Override
                    public void success() {
                        Toast.makeText(getContext(),"Request sent, You will get notification after request accepted",Toast.LENGTH_LONG).show();
                        dismiss();
                    }

                    @Override
                    public void error(String error) {
                        Toast.makeText(getContext(),"failed "+error,Toast.LENGTH_LONG).show();
                        dismiss();
                    }
                });

            }else {
                Toast.makeText(getContext(),"Please enter code",Toast.LENGTH_LONG).show();
            }
        });
    }

}
