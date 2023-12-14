package com.ironman.tracker.utills;

import com.ironman.tracker.valueHolder.UserValueHolder;

import java.util.ArrayList;

public interface OnGetTrackingUsers {
    void onUserGetComplete(ArrayList<UserValueHolder> valueHolders);
    void error();
}
