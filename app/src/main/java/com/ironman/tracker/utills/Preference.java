package com.ironman.tracker.utills;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class Preference {
    private SharedPreferences sharedPreferences;
    final private String BACKGROUND_SERVICE="background_service";
    final private String LOCATION_TRACKING="location_tracking";
    final private String CALL_LOG_TRACKING="call_log";
    final private String APP_USAGE_TRACKING="app_usage";
    final private String APP_PREF="app_pref";
    public final static String TAG="MY_LOG";
    public final static String XX="MY_LOG";



    public Preference(Context context){
        this.sharedPreferences=context.getSharedPreferences(APP_PREF,Context.MODE_PRIVATE);
    }

    public void offAllTracking(){
        setBackgroundTracking(false);
        setLocationTracking(false);
        setCallLogTracking(false);
        setAppUsageTracking(false);
    }

    public void setBackgroundTracking(boolean isEnable){
        Log.d(TAG, "setBackgroundTracking: "+isEnable);
        sharedPreferences.edit().putBoolean(BACKGROUND_SERVICE,isEnable).apply();
    }

    public boolean isBackgroundTrackingEnabled(){
        Log.d(TAG, "isBackgroundTrackingEnabled: "+sharedPreferences.getBoolean(BACKGROUND_SERVICE,false));
        return sharedPreferences.getBoolean(BACKGROUND_SERVICE,false);
    }


    public void setLocationTracking(boolean isEnable){
        Log.d(TAG, "setLocationTracking: "+isEnable);
        sharedPreferences.edit().putBoolean(LOCATION_TRACKING,isEnable).apply();
    }

    public boolean isLocationTrackingEnabled(){
        Log.d(TAG, "isLocationTrackingEnabled: "+sharedPreferences.getBoolean(LOCATION_TRACKING,false));
        return sharedPreferences.getBoolean(LOCATION_TRACKING,false);
    }

    public void setCallLogTracking(boolean isEnable){
        Log.d(TAG, "setCallLogTracking: "+isEnable);
        sharedPreferences.edit().putBoolean(CALL_LOG_TRACKING,isEnable).apply();
    }

    public boolean isCallLogTrackingEnabled(){
        Log.d(TAG, "isCallLogTrackingEnabled: "+sharedPreferences.getBoolean(CALL_LOG_TRACKING,false));
        return sharedPreferences.getBoolean(CALL_LOG_TRACKING,false);
    }
    public void setAppUsageTracking(boolean isEnable){
        Log.d(TAG, "setAppUsageTracking: "+isEnable);
        sharedPreferences.edit().putBoolean(APP_USAGE_TRACKING,isEnable).apply();
    }

    public boolean isAppUsageTrackingEnabled(){
        Log.d(TAG, "isAppUsageTrackingEnabled: "+sharedPreferences.getBoolean(APP_USAGE_TRACKING,false));
        return sharedPreferences.getBoolean(APP_USAGE_TRACKING,false);
    }
}
