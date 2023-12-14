package com.ironman.tracker.bottomSheets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.ironman.tracker.R;
import com.ironman.tracker.utills.OnAlertDialogListener;

public class AlertDialogBottomSheet extends BottomSheetDialogFragment {
    private OnAlertDialogListener onAlertDialogListener;
    private String title,subtitle;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.alert_bottom_layout, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((TextView)view.findViewById(R.id.title)).setText(this.title);
        ((TextView)view.findViewById(R.id.subtitle)).setText(this.subtitle);

        view.findViewById(R.id.cancel_button).setOnClickListener(v -> {
            onAlertDialogListener.no();
            dismiss();
        });
        view.findViewById(R.id.confirm_button).setOnClickListener(v -> {
            onAlertDialogListener.yes();
            dismiss();
        });

    }

    public AlertDialogBottomSheet( String title, String subtitle,OnAlertDialogListener onAlertDialogListener) {
        this.onAlertDialogListener = onAlertDialogListener;
        this.title = title;
        this.subtitle = subtitle;
    }
}
