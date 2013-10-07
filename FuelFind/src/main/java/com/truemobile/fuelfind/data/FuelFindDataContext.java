package com.truemobile.fuelfind.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.telephony.CellSignalStrengthGsm;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Werner on 2013/10/07.
 */
public class FuelFindDataContext extends SQLiteOpenHelper {

    final static int DB_VERSION = 1;
    final static String DB_NAME = "FuelFindDB.s3db";
    Context context;

    final static String SERVICE_STATION_TABLE = "service_station";
    final static String SERVICE_STATION_ID = "_id";
    final static String SERVICE_STATION_NAME = "name";
    final static String SERVICE_STATION_LAT = "Lat";
    final static String SERVICE_STATION_LNG = "Lng";
    final static String SERVICE_STATION_FIELD_ONE = "field_one";
    final static String SERVICE_STATION_FIELD_TWO = "field_two";
    final static String SERVICE_STATION_FIELD_THREE = "field_three";

    final static String SERVICE_STATION = "CREATE TABLE service_station (\n" +
            " _id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            " name TEXT NOT NULL,\n" +
            " Lat REAL NOT NULL,\n" +
            " Lng REAL NOT NULL,\n" +
            " field_one TEXT NOT NULL,\n" +
            " field_two TEXT NOT NULL,\n" +
            " field_three TEXT NOT NULL\n" +
            ");";

    final static String FUEL_TYPE = "CREATE TABLE fuel_type (\n" +
            " _id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            " name TEXT NOT NULL,\n" +
            " field_one TEXT NOT NULL\n" +
            ");";

    final static String FUEL_PRICE = "CREATE TABLE fuel_price (\n" +
            " _id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            " service_station_id INTEGER NOT NULL,\n" +
            " fuel_type_id INTEGER NOT NULL,\n" +
            " price REAL NOT NULL\n" +
            ");";


    public FuelFindDataContext(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        // Store the context for later use
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SERVICE_STATION);
        sqLiteDatabase.execSQL(FUEL_TYPE);
        sqLiteDatabase.execSQL(FUEL_PRICE);

        ContentValues values = new ContentValues();
        values.put(SERVICE_STATION_NAME, "BP");
        values.put(SERVICE_STATION_LAT, "-27.0100099");
        values.put(SERVICE_STATION_LNG, "28.6025098");
        values.put(SERVICE_STATION_FIELD_ONE, "SERVICE_STATION_FIELD_ONE");
        values.put(SERVICE_STATION_FIELD_TWO, "SERVICE_STATION_FIELD_TWO");
        values.put(SERVICE_STATION_FIELD_THREE, "SERVICE_STATION_FIELD_THREE");
        sqLiteDatabase.insert(SERVICE_STATION_TABLE, null, values);

        values = new ContentValues();
        values.put(SERVICE_STATION_NAME, "BP 2");
        values.put(SERVICE_STATION_LAT, "-25.7248999");
        values.put(SERVICE_STATION_LNG, "28.1624790");
        values.put(SERVICE_STATION_FIELD_ONE, "SERVICE_STATION_FIELD_ONE");
        values.put(SERVICE_STATION_FIELD_TWO, "SERVICE_STATION_FIELD_TWO");
        values.put(SERVICE_STATION_FIELD_THREE, "SERVICE_STATION_FIELD_THREE");
        sqLiteDatabase.insert(SERVICE_STATION_TABLE, null, values);

        values = new ContentValues();
        values.put(SERVICE_STATION_NAME, "BP 3");
        values.put(SERVICE_STATION_LAT, "-25.6486795");
        values.put(SERVICE_STATION_LNG, "28.0914297");
        values.put(SERVICE_STATION_FIELD_ONE, "SERVICE_STATION_FIELD_ONE");
        values.put(SERVICE_STATION_FIELD_TWO, "SERVICE_STATION_FIELD_TWO");
        values.put(SERVICE_STATION_FIELD_THREE, "SERVICE_STATION_FIELD_THREE");
        sqLiteDatabase.insert(SERVICE_STATION_TABLE, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }


    public List<ServiceStation> GetAllServiceStations() {
        List<ServiceStation> mServiceStationList = new ArrayList<ServiceStation>();
        String select = "SELECT * FROM " + SERVICE_STATION_TABLE;

        SQLiteDatabase DB = this.getReadableDatabase();

        Cursor mCursor = DB.rawQuery(select, null);

        if (mCursor.moveToFirst()) {
            do {
                ServiceStation current = new ServiceStation();
                current.id = Integer.parseInt(mCursor.getString(mCursor.getColumnIndex(SERVICE_STATION_ID)));
                current.Lat = Double.parseDouble(mCursor.getString(mCursor.getColumnIndex(SERVICE_STATION_LAT)));
                current.Lng = Double.parseDouble(mCursor.getString(mCursor.getColumnIndex(SERVICE_STATION_LNG)));
                current.name = mCursor.getString(mCursor.getColumnIndex(SERVICE_STATION_NAME));
                current.field_one = mCursor.getString(mCursor.getColumnIndex(SERVICE_STATION_FIELD_ONE));
                current.field_two = mCursor.getString(mCursor.getColumnIndex(SERVICE_STATION_FIELD_TWO));
                current.field_three = mCursor.getString(mCursor.getColumnIndex(SERVICE_STATION_FIELD_THREE));
                mServiceStationList.add(current);
            } while (mCursor.moveToNext());
        }
        return mServiceStationList;
    }


}