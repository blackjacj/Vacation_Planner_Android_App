package com.example.d308vacationplanner.UI;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class MyReceiver extends BroadcastReceiver {

    String channelID = "test";

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, intent.getStringExtra("key"), Toast.LENGTH_LONG).show();
        createNotificationChannel(context, channelID);
        Notification n = new NotificationCompat.Builder(context, channelID)
                .setSmallIcon(android.R.drawable.ic_popup_reminder)
                .setContentText(intent.getStringExtra("key"))
                .setContentTitle("Notification Text").build();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        int uniqueNotificationID = (int) System.currentTimeMillis();
        notificationManager.notify(uniqueNotificationID, n);

    }

    private void createNotificationChannel(Context context, String channelID) {
        CharSequence name = "myChannelName";
        String description = "myChannelDescription";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(channelID, name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }
}
