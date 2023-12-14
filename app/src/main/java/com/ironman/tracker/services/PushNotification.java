package com.ironman.tracker.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ironman.tracker.R;
import com.ironman.tracker.activities.SplashActivity;
import com.ironman.tracker.database.Firebase;

public class PushNotification extends FirebaseMessagingService {
    private String channelId = "Post notifications";
    private NotificationManager mNotificationManager;
    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Firebase.updateFcmToken(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("MY_LOG", "onMessageReceived: "+remoteMessage.getData());
        int requestID = (int) System.currentTimeMillis();
        String message=remoteMessage.getData().get("title");
        String description=remoteMessage.getData().get("title");
        Intent intent=new Intent(getApplicationContext(), SplashActivity.class);
        showNotification(intent,message,description,requestID);


    }

    public void showNotification(Intent intent, String message, String description, int requestId) {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext(), channelId);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(message);
        bigText.setBigContentTitle(description);
        bigText.setSummaryText(description);

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
        mBuilder.setContentTitle(message);
        mBuilder.setContentText(description);
        mBuilder.setPriority(Notification.PRIORITY_DEFAULT);
        mBuilder.setStyle(bigText);
        //
        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //
        String channelId = getString(R.string.app_name);
        NotificationChannel channel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = new NotificationChannel(
                    channelId,
                    "Notification",
                    NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }

        mNotificationManager.notify(requestId, mBuilder.build());

    }
}
