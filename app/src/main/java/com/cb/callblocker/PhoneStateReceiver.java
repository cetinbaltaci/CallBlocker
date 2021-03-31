package com.cb.callblocker;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telecom.TelecomManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;
import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

public class PhoneStateReceiver extends BroadcastReceiver {

    public interface PhoneStateEventListener {
        public void onPhoneHangup(String number) ;
    }

    public PhoneStateReceiver() {

    }

    private PhoneStateEventListener phoneStateEventListener;

    public void setPhoneStateEventListener(PhoneStateEventListener listener) {
        phoneStateEventListener = listener ;
    }


    @Override
    public void onReceive(Context context, Intent intent) {

        try {
            SharedPreferences sharedPref = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
            String countryCode  = sharedPref.getString("CountryCode", "0");
            if (countryCode == "0") return ;


            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            if ( number ==  null ) return;
            SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(context);
            boolean inCallInternational = (shared.getBoolean("inCallInternational", false));
            boolean inCallDomestic = (shared.getBoolean("inCallDomestic", false));
            boolean block = false ;

            boolean internationalNumber = false ;

            if (number.startsWith("+") && !number.startsWith("+" + countryCode)) internationalNumber = true  ;
            if (number.startsWith("00") && !number.startsWith("00" + countryCode)) internationalNumber = true  ;

            if (inCallInternational && internationalNumber )  block = true ;
            if (inCallDomestic && !internationalNumber )  block = true ;

            if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING)) {
                try {
                    if (block) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            TelecomManager telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
                            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ANSWER_PHONE_CALLS) == PackageManager.PERMISSION_GRANTED) {
                                telecomManager.endCall();
                            }
                        } else {
                            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                            Class c = Class.forName(tm.getClass().getName());
                            Method m = c.getDeclaredMethod("getITelephony");
                            m.setAccessible(true);
                            com.android.internal.telephony.ITelephony telephonyService = (ITelephony) m.invoke(tm);
                            telephonyService.endCall();
                        }
                        //Toast.makeText(context, "Ending the call from: " + number, Toast.LENGTH_SHORT).show();
                        if (phoneStateEventListener != null) phoneStateEventListener.onPhoneHangup(number);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
