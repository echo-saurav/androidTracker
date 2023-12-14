package com.ironman.tracker.getInfo;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.ironman.tracker.database.Firebase;
import com.ironman.tracker.valueHolder.CallLogValueHolder;

import java.util.ArrayList;
import java.util.List;

import me.everything.providers.android.calllog.Call;
import me.everything.providers.android.calllog.CallsProvider;

public class MyCallLog {
    CallsProvider callsProvider;

    public MyCallLog(Context context){
        callsProvider = new CallsProvider(context);
    }

    public ArrayList<CallLogValueHolder> getAllCall(){
        ArrayList<CallLogValueHolder> callLogValueHolders=new ArrayList<>();

        for(Call call:callsProvider.getCalls().getList()){
            callLogValueHolders.add(new CallLogValueHolder(call.name,call.number,call.duration,call.callDate,
                    FirebaseAuth.getInstance().getCurrentUser().getUid()));

        }
        return callLogValueHolders;
    }
}

