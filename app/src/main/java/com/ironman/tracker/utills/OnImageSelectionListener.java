package com.ironman.tracker.utills;


import android.content.Intent;

import androidx.annotation.Nullable;

public interface OnImageSelectionListener {
    boolean isReadPermissionGranted();
    boolean isCameraPermissionGranted();
    void getImageSelectionPermission();
    void getCameraPermission();
    void selectFromGallery();
    void takeFromCamera();
}
