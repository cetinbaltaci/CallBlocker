package com.cb.callblocker;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextClock;
import android.widget.TextView;

import com.google.android.material.button.MaterialButtonToggleGroup;

public class StatusFragment extends Fragment {
    private TextView txtStatus = null;
    private String text1 = null ;
    private String text2 = null ;
    private String text3 = null ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_status, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button btnStartService = (Button) view.findViewById(R.id.btnStartService);
        Button btnStopService = (Button) view.findViewById(R.id.btnStopService);
        btnStartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MainScreen.startService(view.getContext());
                new Thread(runStatus).start();
            }
        });
        btnStopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainScreen.stopService(view.getContext());
                new Thread(runStatus).start();
            }
        });

        txtStatus = (TextView)view.findViewById(R.id.txtServiceStatus);
        //new Thread(runStatus).start();
        text1 = StatusFragment.this.getResources().getString(R.string.text_servicestatus);
        text2 = StatusFragment.this.getResources().getString(R.string.text_servicestatus_running);
        text3 = StatusFragment.this.getResources().getString(R.string.text_servicestatus_stopped);
        txtStatus.setText(String.format("%s: %s",
                text1,
                (CallBlockerService.isServiceCreated() ) ? text2 : text3 ) );

        SharedPreferences sharedPref = getContext().getSharedPreferences(getContext().getPackageName(), Context.MODE_PRIVATE);
        String countryCode  = sharedPref.getString("CountryCode", "0");
        TextView text_countryCode = (TextView)view.findViewById(R.id.text_country_phone_code);
        text_countryCode.setText(countryCode);
    }

    private Runnable runStatus = new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    txtStatus.setText(String.format("%s: %s",
                            text1,
                            (CallBlockerService.isServiceCreated() ) ? text2 : text3 ) );
                }
            });


        }
    };

}