package com.ironman.tracker.services.locationTracking;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.ironman.tracker.R;
import com.ironman.tracker.utills.OnPermissionGrantedListener;

public class LocationPermissionBottomSheet  extends BottomSheetDialogFragment {
    private int REQUEST_CODE = 2021;
    private OnPermissionGrantedListener onPermissionGrantedListener;

    public LocationPermissionBottomSheet(OnPermissionGrantedListener onPermissionGrantedListener) {
        this.onPermissionGrantedListener = onPermissionGrantedListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.location_permission_bottom_sheet, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.button)
                .setOnClickListener(v -> getLocationPermission());
    }


    public void getLocationPermission() {
        if (!isLocationPermissionGranted(getActivity())) {
            //get location permission
            if (getActivity() != null)
                ActivityCompat          // get permission call
                        .requestPermissions(getActivity(), new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);
            //
            //if permission granted
        } else {
            Toast.makeText(getContext(), "Location Permission granted!", Toast.LENGTH_LONG).show();
            dismiss();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                requestCode == REQUEST_CODE)
        {
            onPermissionGrantedListener.isPermissionGranted(true);

        } else {
            onPermissionGrantedListener.isPermissionGranted(false);
        }
        dismiss();
    }


    public static boolean isLocationPermissionGranted(Activity activity) {
        if (activity == null) {
            return false;
        }
        //
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        }
        //
        // always true if bellow version code M
        else {
            return true;
        }
    }

    //
    public static boolean isLocationPermissionGranted(Application application) {
        if (application == null) {
            return false;
        }
        //
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (application.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    application.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        }
        //
        // always true if bellow version code M
        else {
            return true;
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);

    }

}
