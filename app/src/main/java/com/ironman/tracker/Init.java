package com.ironman.tracker;

import android.app.Application;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ironman.tracker.database.Firebase;

public class Init extends Application {

    private static final String TAG = "MY_LOG";

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        Firebase.updateFcmToken(token);

                    });

            FirebaseMessaging.getInstance().subscribeToTopic("all");
        }
    }
}
