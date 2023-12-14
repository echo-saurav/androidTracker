package com.ironman.tracker.valueHolder;

import static com.ironman.tracker.utills.StaticValues.CALL_VALUE;

public class CallLogValueHolder {
    public int type=CALL_VALUE;

    public String name;
    public String number;
    public long duration;
    public long time;
    public String uid;

    public CallLogValueHolder(){}

    public CallLogValueHolder(String name, String number, long duration, long time,String uid) {
        this.name = name;
        this.number = number;
        this.duration = duration;
        this.time = time;
        this.uid=uid;
    }
}
