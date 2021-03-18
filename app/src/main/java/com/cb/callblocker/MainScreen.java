package com.cb.callblocker;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.cb.callblocker.ui.main.SectionsPagerAdapter;

public class MainScreen extends AppCompatActivity {
    TabLayout mTabLayout = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.ANSWER_PHONE_CALLS) == PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.MODIFY_PHONE_STATE) == PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.SYSTEM_ALERT_WINDOW) == PackageManager.PERMISSION_DENIED) {
                String[] permissions = {Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.ANSWER_PHONE_CALLS,
                        Manifest.permission.MODIFY_PHONE_STATE,
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.READ_CALL_LOG,
                        Manifest.permission.SYSTEM_ALERT_WINDOW};
                requestPermissions(permissions, 1);
            }
        }

        String countryCode = getCountryDialCode();
        SharedPreferences sharedPref = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPref.edit();
        edit.putString("CountryCode", countryCode);
        edit.commit();
        Log.d("PHONE", "countryCode: " + countryCode);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(viewPager);

        mTabLayout.setSelectedTabIndicatorColor(Color.parseColor("#FF0000"));
        mTabLayout.setTabTextColors(Color.parseColor("#727272"), Color.parseColor("#ffffff"));
        mTabLayout.selectTab(mTabLayout.getTabAt(0));

        if ( CallBlockerService.isServiceCreated() )
            updateServiceNotification();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mTabLayout.selectTab(mTabLayout.getTabAt(0));
        if ( CallBlockerService.isServiceCreated() )
            updateServiceNotification();
    }

    public static boolean startService(Context context) {
        if (CallBlockerService.isServiceCreated()) {
            Toast.makeText(context, context.getResources().getString(R.string.text_notification), Toast.LENGTH_SHORT).show();
            return true;
        }
        Intent serviceIntent = new Intent(context, CallBlockerService.class);
        ContextCompat.startForegroundService(context, serviceIntent);
        return CallBlockerService.isServiceCreated();
    }
    public static boolean stopService(Context context) {
        Intent serviceIntent = new Intent(context, CallBlockerService.class);
        context.stopService(serviceIntent);
        return (! CallBlockerService.isServiceCreated() );
    }

    public String getCountryDialCode(){
        String contryId = null;
        String contryDialCode = null;

        TelephonyManager telephonyMngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        contryId = telephonyMngr.getSimCountryIso().toUpperCase();

        contryDialCode = Util.getCountryDialCode(contryId);

        return contryDialCode;
    }

    private Notification getServiceNotification(String text) {
        String title = getResources().getString(R.string.title_notification);
        Intent notificationIntent = new Intent(this, MainScreen.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CallBlockerService.CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentIntent(pendingIntent)
                .build();

        return notification;
    }

    public void updateServiceNotification() {
        String msg = getResources().getString(R.string.text_notification);
        Notification notification = getServiceNotification(msg);
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.notify(CallBlockerService.NOTIF_ID, notification);
    }

}