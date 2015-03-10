package com.cristina.timezonealarm.data;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Cristina on 10-Mar-15.
 */
public class AlarmsTable {

    // Database table
    public static final String TABLE_ALARMS = "alarms";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TIMEZONEID = "timezone_id";
    public static final String COLUMN_HOUR = "hour";
    public static final String COLUMN_MINUTE = "minute";
    public static final String COLUMN_TIMEOFDAY = "timeofday";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_ANGLE = "angle";
    public static final String COLUMN_ACTIVE = "active";

    // Database creation SQL statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_ALARMS
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_TIMEZONEID + " text not null, "
            + COLUMN_HOUR + " integer not null,"
            + COLUMN_MINUTE  + " integer not null,"
            + COLUMN_TIMEOFDAY  + " integer not null,"
            + COLUMN_ANGLE  + " real not null,"
            + COLUMN_TITLE  + " text not null,"
            + COLUMN_ACTIVE  + " integer not null"
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(AlarmsTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_ALARMS);
        onCreate(database);
    }
}
