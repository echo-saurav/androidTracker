package com.ironman.tracker.valueHolder;

import static com.ironman.tracker.utills.StaticValues.LOCATION_VALUE;

public class LocationValueHolder {
    public int type=LOCATION_VALUE;

    public String address;
    public double lat;
    public double lon;
    public long time;
    public String uid;


    public LocationValueHolder(){}

    public LocationValueHolder(String address,double lat, double lon, long time,String uid) {
        this.address=address;
        this.lat = lat;
        this.lon = lon;
        this.time = time;
        this.uid=uid;
    }
}
