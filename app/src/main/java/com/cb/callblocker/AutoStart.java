package com.cb.callblocker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class AutoStart extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(context);
        boolean autoStart = (shared.getBoolean("autoStart", false));
        if (autoStart) {
            MainScreen.startService(context);
        }
    }
}
