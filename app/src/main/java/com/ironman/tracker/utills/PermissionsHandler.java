package com.ironman.tracker.utills;

import android.content.Context;

import androidx.fragment.app.FragmentManager;

import com.ironman.tracker.bottomSheets.AlertDialogBottomSheet;

class PermissionsHandler {
    private String[] permissions;
    private String title,subtitle;
    private int REQ_CODE=930;
    private FragmentManager fragmentManager;
    private Context context;

    public PermissionsHandler(String[] permissions, String title, String subtitle, FragmentManager fragmentManager, Context context) {
        this.permissions = permissions;
        this.title = title;
        this.subtitle = subtitle;
        this.fragmentManager = fragmentManager;
        this.context = context;
    }

    public void takePermission(OnPermissionGrantedListener onPermissionGrantedListener){
        AlertDialogBottomSheet alertDialogBottomSheet=new AlertDialogBottomSheet(this.title, this.subtitle,
                new OnAlertDialogListener() {
                    @Override
                    public void yes() {

                    }

                    @Override
                    public void no() {

                    }
                });
        alertDialogBottomSheet.show(fragmentManager,"permission");;
    }

    void reqPermission(){


    }

}
