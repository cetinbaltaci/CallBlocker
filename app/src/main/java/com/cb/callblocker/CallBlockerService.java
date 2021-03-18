package com.cb.callblocker;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class CallBlockerService extends Service {
    public static final String CHANNEL_ID = "CallBlockerServiceChannel";
    public static final int NOTIF_ID = 1;

    PhoneStateReceiver phoneStateReceiver;
    private static CallBlockerService instance = null;

    public CallBlockerService() {
    }


    public static boolean isServiceCreated() {
        return (instance != null) ;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PHONE_STATE");
        intentFilter.setPriority(100);
        phoneStateReceiver = new PhoneStateReceiver();
        // Register the broadcast receiver with the intent filter object.
        phoneStateReceiver.setPhoneStateEventListener(new PhoneStateReceiver.PhoneStateEventListener() {
            @Override
            public void onPhoneHangup(String number) {
                long unixTime = System.currentTimeMillis() / 1000L;
                CallData callData = new CallData(0, unixTime, number);
                SQLiteDatabaseHandler.getInstance(CallBlockerService.this).addCallData(callData);
                updateServiceNotification();
            }
        });
        registerReceiver(phoneStateReceiver, intentFilter);
        instance = this ;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String msg = getResources().getString(R.string.text_notification);
        createNotificationChannel();
        Notification notification = getServiceNotification( msg);
        startForeground(NOTIF_ID, notification);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(phoneStateReceiver!=null) {
            unregisterReceiver(phoneStateReceiver);
        }
        instance = null;
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Call Blocker Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private Notification getServiceNotification( String text) {
        String title = getResources().getString(R.string.title_notification);
        Intent notificationIntent = new Intent(this, MainScreen.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentIntent(pendingIntent)
                .build();
        return notification;
    }

    private void updateServiceNotification() {
        String msg = getResources().getString(R.string.text_notification);
        String msg_count = getResources().getString(R.string.text_notification_count);

        if (SQLiteDatabaseHandler.getInstance(this).getRecentRowCount() > 0 )
            msg  += String.format(" %s: %d" ,msg_count, SQLiteDatabaseHandler.getInstance(this).getRecentRowCount());

        Notification notification = getServiceNotification(msg);
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.notify(NOTIF_ID, notification);
    }
}