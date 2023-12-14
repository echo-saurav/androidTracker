package com.ironman.tracker.fragments;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.ironman.tracker.bottomSheets.AlertDialogBottomSheet;
import com.ironman.tracker.R;
import com.ironman.tracker.activities.SplashActivity;
import com.ironman.tracker.services.BackgroundTask;
import com.ironman.tracker.utills.OnAlertDialogListener;
import com.ironman.tracker.utills.OnFragmentChangeListener;
import com.ironman.tracker.utills.Preference;

import static com.ironman.tracker.utills.Preference.TAG;

public class SettingFragment extends Fragment {
    private OnFragmentChangeListener onFragmentChangeListener;
    private Preference preference;
    private Switch backgroundTracking,location,appUsage,callLogs;
    private final int LOCATION_REQUEST_CODE =200;
    private final int CALL_LOGS_REQUEST_CODE=400;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.setting_layout,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.logout_button).setOnClickListener(v -> logout());
        location=view.findViewById(R.id.location_switch);
        appUsage=view.findViewById(R.id.app_usage_switch);
        callLogs=view.findViewById(R.id.call_logs_switch);
        backgroundTracking=view.findViewById(R.id.background_service_switch);

        if(getContext()==null) return;
        if(getActivity()==null) return;

        preference=new Preference(getContext());
        location.setChecked(preference.isLocationTrackingEnabled());
        appUsage.setChecked(preference.isAppUsageTrackingEnabled());
        callLogs.setChecked(preference.isCallLogTrackingEnabled());
        backgroundTracking.setChecked(isServiceRunning(BackgroundTask.class));

        location.setOnCheckedChangeListener((buttonView, isChecked) -> setLocation(isChecked));
        appUsage.setOnCheckedChangeListener((buttonView, isChecked) -> setAppUsage(isChecked));
        callLogs.setOnCheckedChangeListener((buttonView, isChecked) -> setCallLogs(isChecked));

        backgroundTracking.setOnCheckedChangeListener((buttonView, isChecked) -> setBackgroundTracking(isChecked));

    }

    void setCallLogs(boolean isChecked){
        if(isCallLogsPermissionGranted(getContext())){
            Log.d(TAG, "setCallLogs: permission granted");
            preference.setCallLogTracking(isChecked);
            callLogs.setChecked(isChecked);
        }else {
            // get permission
            Log.d(TAG, "setCallLogs: permission not granted");
            callLogs.setChecked(false);
            new AlertDialogBottomSheet("Call Logs", "To share your call logs, you need to give call logs permission",
                    new OnAlertDialogListener() {
                        @Override
                        public void yes() {
                            if(getActivity()==null) return;
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CALL_LOG}, CALL_LOGS_REQUEST_CODE);
                        }

                        @Override
                        public void no() {
                            Toast.makeText(getContext(),"Call logs permission getting failed",Toast.LENGTH_LONG).show();
                        }
                    }).show(getFragmentManager(),"call_log_alert");
        }
    }

    void setBackgroundTracking(boolean isChecked){
        if(getContext()==null || getActivity()==null) return;

        if(isForegroundPermissionGranted(getContext())){
            //
            if(isChecked && !isServiceRunning(BackgroundTask.class)){
                getActivity().startService(new Intent(getContext(),BackgroundTask.class));
                backgroundTracking.setChecked(true);
            }else if(!isChecked && isServiceRunning(BackgroundTask.class)){
                getContext().stopService(new Intent(getContext(),BackgroundTask.class));
                backgroundTracking.setChecked(false);
            }
            //
        }else {
            backgroundTracking.setChecked(false);
            // get permission
            new AlertDialogBottomSheet("Tracking", "To track information in background, you need to give call foreground service permission",
                    new OnAlertDialogListener() {
                        @Override
                        public void yes() {
                            if(getActivity()==null) return;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.FOREGROUND_SERVICE}, CALL_LOGS_REQUEST_CODE);
                            }
                        }

                        @Override
                        public void no() {
                            Toast.makeText(getContext(),"foreground service permission getting failed",Toast.LENGTH_LONG).show();
                        }
                    }).show(getFragmentManager(),"background_tracking_alert");
        }
    }


    void setAppUsage(boolean isChecked){
        if (isAppUsageAccessGranted()) {
            preference.setAppUsageTracking(isChecked);
            appUsage.setChecked(isChecked);

        }else {
            appUsage.setChecked(false);
            // get permission
            new AlertDialogBottomSheet("App Usage States", "To track your app usage, you need to give app usage permission",
                    new OnAlertDialogListener() {
                        @Override
                        public void yes() {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void no() {
                            Toast.makeText(getContext(),"App Usage States permission getting failed",Toast.LENGTH_LONG).show();
                        }
                    }).show(getFragmentManager(),"app_usage_alert");
        }

    }

    void setLocation(boolean isLocation){
        if(isLocationPermissionGranted(getContext())){
            Log.d(TAG, "setLocation: permission granted");
            preference.setLocationTracking(isLocation);
            location.setChecked(isLocation);

        }else {
            Log.d(TAG, "setLocation: permission not granted");
            location.setChecked(false);
            new AlertDialogBottomSheet("Location Permission", "You need location permission to share location. Please give all time access location permission",
                    new OnAlertDialogListener() {
                        @Override
                        public void yes() {
                            if(getActivity()==null) return;

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                                Manifest.permission.ACCESS_FINE_LOCATION,
                                                Manifest.permission.ACCESS_BACKGROUND_LOCATION
                                        }, LOCATION_REQUEST_CODE);
                            }else {
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                                Manifest.permission.ACCESS_FINE_LOCATION
                                        }, LOCATION_REQUEST_CODE);
                            }

                        }
                        @Override
                        public void no() {
                            Toast.makeText(getContext(),"Location permission getting failed",Toast.LENGTH_LONG).show();
                        }
                    }).show(getFragmentManager(),"location_alert");
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            // location permission
            case LOCATION_REQUEST_CODE:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                    if (grantResults.length > 0 &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                            grantResults[2] == PackageManager.PERMISSION_GRANTED) {

                        location.setChecked(preference.isLocationTrackingEnabled());

                    }else {
                        preference.setLocationTracking(false);
                        location.setChecked(false);
                    }

                }else {

                    if (grantResults.length > 0 &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                        location.setChecked(preference.isLocationTrackingEnabled());

                    }else {
                        preference.setLocationTracking(false);
                        location.setChecked(false);
                    }
                }

                break;
            // call logs permission
            case CALL_LOGS_REQUEST_CODE:
                callLogs.setChecked(preference.isCallLogTrackingEnabled());
                break;

            default:
                Toast.makeText(getContext(),"Something is wrong",Toast.LENGTH_LONG).show();
                break;
        }

    }


    private boolean isAppUsageAccessGranted() {
        if(getContext()==null) return false;
        try {
            PackageManager packageManager = getContext().getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getContext().getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager)getContext().getSystemService(Context.APP_OPS_SERVICE);
            int mode = 0;
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.KITKAT) {
                mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                        applicationInfo.uid, applicationInfo.packageName);
            }
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public boolean isLocationPermissionGranted(Context context) {
        if (context == null) return false;
        return (ActivityCompat.checkSelfPermission(context,Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) &&
                (ActivityCompat.checkSelfPermission(context,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    public boolean isCallLogsPermissionGranted(Context context) {
        if (context == null) return false;
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED;
    }

    public boolean isForegroundPermissionGranted(Context context) {
        if (context == null) return false;
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.FOREGROUND_SERVICE) == PackageManager.PERMISSION_GRANTED;
    }



    void logout(){
        if(getFragmentManager()!=null )
            new AlertDialogBottomSheet(
                    "Logout",
                    "Are you sure you want to logout?", new OnAlertDialogListener() {
                @Override
                public void yes() {
                    if(getActivity()==null || getContext()==null) return;

                    FirebaseAuth.getInstance().signOut();
                    new Preference(getContext()).offAllTracking();
                    getActivity().finish();
                    getContext().stopService(new Intent(getContext(),BackgroundTask.class));
                    startActivity(new Intent(getContext(), SplashActivity.class));
                }

                @Override
                public void no() {

                }
            }).show(getFragmentManager(),"alert");
    }

    public SettingFragment(OnFragmentChangeListener onFragmentChangeListener) {
        this.onFragmentChangeListener = onFragmentChangeListener;
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
