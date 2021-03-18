package com.cb.callblocker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CallData {
    private int ID;
    private long CallTime;
    private String Number;
    public CallData() {

    }

    public CallData(int ID, long callTime, String number) {
        this.ID = ID;
        this.CallTime = callTime;
        this.Number = number;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setCallTime(int callTime) {
        CallTime = callTime;
    }

    public void setNumber(String number) {
        Number = number;
    }

    public int getID() {
        return ID;
    }

    public long getCallTime() {
        return CallTime;
    }

    public String getCallDateTime() {
        long dv = Long.valueOf(CallTime)*1000;// its need to be in milisecond
        Date df = new java.util.Date(dv);
        String vv = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.ENGLISH).format(df);
        return vv;
    }

    public String getNumber() {
        return Number;
    }
}
