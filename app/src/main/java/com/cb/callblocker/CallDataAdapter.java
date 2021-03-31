package com.cb.callblocker;

import android.content.Context;
import android.graphics.Color;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CallDataAdapter extends ArrayAdapter<CallData> {

    public CallDataAdapter(Context context, ArrayList<CallData> users) {
        super(context, 0, users);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        CallData callData = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_row, parent, false);
        }

        TextView tvId = (TextView) convertView.findViewById(R.id.colID);
        TextView tvDate = (TextView) convertView.findViewById(R.id.colCallTime);
        TextView tvNumber = (TextView) convertView.findViewById(R.id.colNumber);
        TextView tvName = (TextView) convertView.findViewById(R.id.colName);

        tvId.setText( String.valueOf( callData.getID()));
        tvDate.setText(callData.getCallDateTime());
        //tvDate.setText(String.valueOf(callData.getCallTime()));
        String formattedNumber = PhoneNumberUtils.formatNumber(callData.getNumber(), Locale.getDefault().getCountry());
        if (TextUtils.isEmpty(formattedNumber)) {
            formattedNumber = callData.getNumber() ;
        }
        tvNumber.setText(formattedNumber );

        String[] phoneFormatList = formattedNumber.split(" ");
        String code = "" ;
        String countryID = "---";
        if ( phoneFormatList.length > 0 ) {
            code = phoneFormatList[0];
        }

        if (! TextUtils.isEmpty(code)) {
            if (code.startsWith("+"))
                code = code.substring(1);
            else if (code.equals("00"))
                code = phoneFormatList[1];
            countryID = Util.getCountryDialID(code);
            if (TextUtils.isEmpty(countryID))countryID = "---";
        }
        tvName.setText(countryID);

        if (position % 2 == 0 ) {
            convertView.setBackgroundColor(Color.LTGRAY);
        }else {
            convertView.setBackgroundColor(Color.WHITE);
        }
        return convertView;
    }
}
