package com.cb.callblocker;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class LogsFragment extends Fragment {
    Calendar mStartDate = Calendar.getInstance();
    Calendar mEndDate = Calendar.getInstance();
    private EditText mPhoneNumber ;
    private EditText mEdtDateStart ;
    private EditText mEdtDateEnd ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_logs, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mEdtDateStart = (EditText)view.findViewById(R.id.dateStart);
        mEdtDateStart.setText(new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH).format(mStartDate.getTime()));
        mEdtDateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        mStartDate.set(year, month, dayOfMonth);
                        mEdtDateStart.setText(new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH).format(mStartDate.getTime()));
                    }
                }, mStartDate.get(Calendar.YEAR),mStartDate.get(Calendar.MONTH), mStartDate.get(Calendar.DAY_OF_MONTH)) ;
                datePickerDialog.setTitle(getResources().getString(R.string.title_start_date));
                datePickerDialog.show();
            }
        });

        mEdtDateEnd = (EditText)view.findViewById(R.id.dateEnd);
        mEdtDateEnd.setText(new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH).format(mEndDate.getTime()));
        mEdtDateEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        mEndDate.set(year, month, dayOfMonth);
                        mEdtDateEnd.setText(new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH).format(mEndDate.getTime()));
                    }
                }, mEndDate.get(Calendar.YEAR),mEndDate.get(Calendar.MONTH), mEndDate.get(Calendar.DAY_OF_MONTH))  ;
                datePickerDialog.setTitle(getResources().getString(R.string.title_end_date));
                datePickerDialog.show();
            }
        });

        Button btnSearch = (Button)view.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)view. getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mPhoneNumber.getWindowToken(),
                        InputMethodManager.RESULT_UNCHANGED_SHOWN);

                searchData();
            }
        });

        mPhoneNumber = (EditText)view.findViewById(R.id.textSearchNumber);
        mPhoneNumber.setText("");
        searchData();

    }
    private void searchData() {
        mStartDate.set(Calendar.HOUR_OF_DAY, 0);
        mStartDate.set(Calendar.MINUTE, 0);
        mStartDate.set(Calendar.SECOND, 0);
        mStartDate.set(Calendar.MILLISECOND, 0);

        mEndDate.set(Calendar.HOUR_OF_DAY, 23);
        mEndDate.set(Calendar.MINUTE, 59);
        mEndDate.set(Calendar.SECOND, 59);
        mEndDate.set(Calendar.MILLISECOND, 999);


        String number = mPhoneNumber.getText().toString();
        if ( TextUtils.isEmpty(number) )
            number = "%";
        long startDate = mStartDate.getTimeInMillis() / 1000;
        long endDate = mEndDate.getTimeInMillis() / 1000;


        ArrayList<CallData> arrayList = SQLiteDatabaseHandler.getInstance(getContext()).allCallData(startDate, endDate, number);
        ListView lv = (ListView) getView().findViewById(R.id.lstCalls);
        CallDataAdapter callDataAdapter = new CallDataAdapter(getContext(), arrayList);
        lv.setAdapter(callDataAdapter);
        //if ( CallBlockerService.isServiceCreated() )
        //    updateServiceNotification();

    }

}