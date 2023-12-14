package com.ironman.tracker.valueHolder;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class RequestValueHolder {
    public  @ServerTimestamp Date timestamp;
    public String uid;

    public RequestValueHolder(){}
    public RequestValueHolder( String uid) {
        this.uid = uid;
    }
}
