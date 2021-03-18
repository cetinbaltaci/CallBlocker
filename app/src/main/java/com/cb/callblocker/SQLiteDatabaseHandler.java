package com.cb.callblocker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteDatabaseHandler extends SQLiteOpenHelper {

    private static SQLiteDatabaseHandler instance = null;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "callDB";
    private static final String TABLE_NAME = "Calls";
    private static final String KEY_ID = "id";
    private static final String KEY_DATE = "calltime";
    private static final String KEY_NUMBER = "number";

    private int mRecentRowCount = 0 ;
    private static final String[] COLUMNS = { KEY_ID, KEY_DATE, KEY_NUMBER };

    private SQLiteDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mRecentRowCount = 0 ;
    }

    public static SQLiteDatabaseHandler getInstance(Context context) {
        if (instance == null)
            instance = new SQLiteDatabaseHandler(context);
        return instance ;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATION_TABLE = "CREATE TABLE "+  TABLE_NAME + " ( "
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_DATE  + " INTEGER, "
                + KEY_NUMBER + " TEXT )";

        db.execSQL(CREATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // you can implement here migration process
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(db);
    }

    public void deleteOne(CallData callData) {
        // Get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "id = ?", new String[] { String.valueOf(callData.getID()) });
        db.close();
    }

    public CallData getCallData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, // a. table
                COLUMNS, // b. column names
                " id = ?", // c. selections
                new String[] { String.valueOf(id) }, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit

        if (cursor != null)
            cursor.moveToFirst();

        CallData callData = new CallData(
                Integer.parseInt(cursor.getString(0)) ,
                Integer.parseInt(cursor.getString(1)),
                cursor.getString(2));
        return callData;
    }

    public ArrayList<CallData> allCallData() {

        ArrayList<CallData> callDatas = new ArrayList<CallData>();
        String query = "SELECT  * FROM " + TABLE_NAME + " ORDER BY " + KEY_DATE + " DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        CallData callData = null;

        if (cursor.moveToFirst()) {
            do {
                callData = new CallData(
                        Integer.parseInt(cursor.getString(0)) ,
                        Integer.parseInt(cursor.getString(1)),
                        cursor.getString(2));

                callDatas.add(callData);
            } while (cursor.moveToNext());
        }

        mRecentRowCount = 0 ;

        return callDatas;
    }

    public ArrayList<CallData> allCallData(long startDate, long endDate, String number) {

        ArrayList<CallData> callDatas = new ArrayList<CallData>();
        String query = "SELECT  * FROM " + TABLE_NAME
                + " WHERE 1 = 1"
                + " AND calltime BETWEEN " + String.valueOf(startDate) + " AND " + String.valueOf(endDate)
                + " AND number LIKE '"+ number + "'"
                + " ORDER BY " + KEY_DATE + " DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        CallData callData = null;

        if (cursor.moveToFirst()) {
            do {
                callData = new CallData(
                        Integer.parseInt(cursor.getString(0)) ,
                        Integer.parseInt(cursor.getString(1)),
                        cursor.getString(2));

                callDatas.add(callData);
            } while (cursor.moveToNext());
        }

        mRecentRowCount = 0 ;

        return callDatas;
    }


    public void addCallData(CallData callData) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_DATE, callData.getCallTime());
        values.put(KEY_NUMBER, callData.getNumber());

        db.insert(TABLE_NAME,null, values);
        db.close();
        mRecentRowCount++ ;
    }

    public int getRecentRowCount() {
        return mRecentRowCount ;
    }

    public int updateCallData(CallData callData) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_DATE, callData.getCallTime());
        values.put(KEY_NUMBER, callData.getNumber());

        int i = db.update(TABLE_NAME, // table
                values, // column/value
                "id = ?", // selections
                new String[] { String.valueOf(callData.getID()) });

        db.close();
        return i;
    }



}