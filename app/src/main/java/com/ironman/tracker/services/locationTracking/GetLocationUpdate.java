package com.ironman.tracker.services.locationTracking;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.util.List;

import static android.content.Context.LOCATION_SERVICE;

public class GetLocationUpdate {
    public enum LOCATION_UPDATER_TYPE{ONCE,CONTINUES}
    private Context context;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private OnLocationUpdateListener onLocationUpdateListener;
    private long locationUpdateIntervalInMilliseconds;
    private LOCATION_UPDATER_TYPE location_updater_type;

    public GetLocationUpdate(Context context, LOCATION_UPDATER_TYPE location_updater_type,long locationUpdateIntervalInMilliseconds ,OnLocationUpdateListener onLocationUpdate) {
        this.context = context;
        this.onLocationUpdateListener = onLocationUpdate;
        this.location_updater_type=location_updater_type;
        this.locationUpdateIntervalInMilliseconds =locationUpdateIntervalInMilliseconds;
    }


    public void updateLocation() {
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location.hasAccuracy()) {
                    Log.d("MY_LOG", "onLocationChanged: "+location);
                    onLocationUpdateListener.currentLocation(location);

                    if(location_updater_type==LOCATION_UPDATER_TYPE.ONCE){
                        Log.d("MY_LOG", "remove update: "+location);
                        locationManager.removeUpdates(this);
                    }
                }

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {
//                Toast.makeText(context, "Location is not enabled, please turn on location for tracker app", Toast.LENGTH_SHORT).show();
//                context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        };

        //if permission not granted
        if (ActivityCompat.checkSelfPermission
                (context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //permission granted
        List<String> allProviders = locationManager.getAllProviders();
        for (String name : allProviders) {
            locationManager.requestLocationUpdates(name, locationUpdateIntervalInMilliseconds, 0, locationListener);
        }
    }

    public void stopListening() { locationManager.removeUpdates(locationListener); }

    public interface OnLocationUpdateListener {
        void currentLocation(Location location);
    }
}
