package com.ironman.tracker.valueHolder;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class NotificationValueHolder {
    public  @ServerTimestamp
    Date timeStamp;
    public String uid;
    public String title;

    public NotificationValueHolder(){}



}
