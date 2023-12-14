package com.ironman.tracker.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ironman.tracker.utills.Preference;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Preference preference=new Preference(context);
        if(preference.isBackgroundTrackingEnabled()){

            context.startService(new Intent(context,BackgroundTask.class));
        }
    }
}
