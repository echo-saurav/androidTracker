package com.ironman.tracker.valueHolder;

import static com.ironman.tracker.utills.StaticValues.APP_USAGE_VALUE;

public class AppUsageValueHolder {
    public int type=APP_USAGE_VALUE;

    public long totalTimeVisible;
    public String appName;
    public String packageName;
//    public byte[] imageByte;
    public long time;
    public String uid;

    public AppUsageValueHolder(){ }

    public AppUsageValueHolder(long totalTimeVisible, long lastTimeVisible, String appName, String packageName,String uid) {
        this.totalTimeVisible = totalTimeVisible;
        this.time = lastTimeVisible;
        this.packageName = packageName;
        this.uid=uid;
        this.appName=appName;
    }


}
