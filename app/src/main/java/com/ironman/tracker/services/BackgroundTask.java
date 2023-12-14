package com.ironman.tracker.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.ironman.tracker.R;
import com.ironman.tracker.activities.SplashActivity;
import com.ironman.tracker.database.Firebase;
import com.ironman.tracker.getInfo.AppUsages;
import com.ironman.tracker.getInfo.MyCallLog;
import com.ironman.tracker.services.locationTracking.GetLocationUpdate;
import com.ironman.tracker.utills.Preference;
import com.ironman.tracker.valueHolder.LocationValueHolder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class BackgroundTask extends Service {
    final private long milliSecond =1000;
    final private String NOTIFICATION_CHANNEL_ID = "com.ironman.tracker.services";
    private Runnable runnable;
    private Handler handler;
    private String TAG="MY_LOG";
    private Preference preference;
    private long second = 60;

    @Override
    public void onCreate() {
        super.onCreate();
        preference=new Preference(getApplicationContext());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        this.handler = new Handler();
        runnable = () -> {
            tick();
            handler.postDelayed(runnable, milliSecond* second);
        };
        handler.post(runnable);

        startForegroundService();
        return START_STICKY;
    }

    void tick(){
        Log.d(TAG, "ticking... ");
        if(preference.isCallLogTrackingEnabled()){

            Firebase.updateTrackingInfo(new MyCallLog(getApplicationContext()).getAllCall());
        }
        if(preference.isAppUsageTrackingEnabled()){

            Firebase.updateAppUsageInfo(new AppUsages().getAppUsageData(getApplicationContext()));
        }
        if(preference.isLocationTrackingEnabled()){
            Log.d(TAG, "tick: location enabled");
            GetLocationUpdate getLocationUpdate=new GetLocationUpdate(getApplicationContext(),
                    GetLocationUpdate.LOCATION_UPDATER_TYPE.ONCE,
                    0,
                    location -> {

                        Firebase.addNewLocationUpdate(
                                new LocationValueHolder(
                                        getAddress(location.getLatitude(),location.getLongitude()),
                                        location.getLatitude(),
                                        location.getLongitude(),
                                        System.currentTimeMillis(),
                                        FirebaseAuth.getInstance().getCurrentUser().getUid()
                                )
                        );

                    }
            );
            getLocationUpdate.updateLocation();
        }

    }

    String getAddress(double latitude,double longitude){
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());


        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            if(addresses.isEmpty()){
                return "unknown address latitude: "+latitude+"lon: "+longitude;
            }
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

          return address+"\n"+knownName;


        } catch (IOException e) {
            e.printStackTrace();
        }
        return "unknown address latitude: "+latitude+"lon: "+longitude;

    }


    private void startForegroundService() {
        String channelName = "Tracking activity";
        NotificationChannel chan = null;
        ///
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            chan.setLightColor(Color.BLUE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        }
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(chan);
        }

        Notification notification=getNotification();
        startForeground(2, notification);
    }

    private Notification getNotification(){
        Intent intent = new Intent(this, SplashActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notification
                .setSubText("Tracking user")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setOngoing(true)
                .setContentIntent(pi)
                .setPriority(NotificationCompat.PRIORITY_MIN) //MIN so it's not shown in the status bar before Oreo, on Oreo it will be bumped to LOW
                .setShowWhen(false)
                .setAutoCancel(false);
        notification.setGroup("BackgroundService");

        return notification.build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: service destroying");
        if(runnable!=null){
            handler.removeCallbacks(runnable);
        }
    }
}
