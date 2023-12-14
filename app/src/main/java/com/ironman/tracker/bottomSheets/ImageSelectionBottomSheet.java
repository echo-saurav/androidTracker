package com.ironman.tracker.bottomSheets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.ironman.tracker.R;
import com.ironman.tracker.utills.OnImageSelectionListener;

public  class ImageSelectionBottomSheet  extends BottomSheetDialogFragment {
    private OnImageSelectionListener onImageSelectionListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.select_image_layout, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.select_image_from_gallery).setOnClickListener(v -> {
            if(onImageSelectionListener.isReadPermissionGranted()){
                onImageSelectionListener.selectFromGallery();
            }else {
                onImageSelectionListener.getImageSelectionPermission();
            }
        });

        view.findViewById(R.id.select_image_from_camera).setOnClickListener(v -> {
            if(onImageSelectionListener.isCameraPermissionGranted()){
                onImageSelectionListener.takeFromCamera();
            }else {
                onImageSelectionListener.getCameraPermission();
            }
        });
    }

    public ImageSelectionBottomSheet(OnImageSelectionListener onImageSelectionListener) {
        this.onImageSelectionListener = onImageSelectionListener;
    }
}