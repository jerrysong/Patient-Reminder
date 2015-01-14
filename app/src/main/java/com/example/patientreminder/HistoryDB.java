package com.example.patientreminder;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

public class HistoryDB implements BaseColumns {

	public static final String TABLE_NAME = "history";
	public static final String LOG_TAG = "historyDB";

	public static final String KEY_DATE = "date";
	public static final String KEY_USER = "user";
	public static final String KEY_MEDICINE = "medicine";
	public static final String KEY_DOSE = "dose";
	public static final String KEY_IMAGE = "image";
	public static final String KEY_ACTUAL_TIME = "actualTime";
	public static final String KEY_ACTUAL_TIME_ZONE = "actualTimeZone";
	public static final String KEY_SCHEDULED_TIME = "scheduledTime";
	public static final String KEY_SCHEDULED_TIME_ZONE = "scheduledTimeZone";
	public static final String KEY_COMPLETITION = "completition";

	public static final String CREATE_SQL = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_NAME + "(" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ KEY_DATE + "," 
			+ KEY_USER + "," 
			+ KEY_MEDICINE + "," 
			+ KEY_DOSE + ","
			+ KEY_IMAGE + ","
			+ KEY_ACTUAL_TIME	+ "," 
			+ KEY_ACTUAL_TIME_ZONE + "," 
			+ KEY_SCHEDULED_TIME	+ "," 
			+ KEY_SCHEDULED_TIME_ZONE + "," 
			+ KEY_COMPLETITION + ")";

	public static final String DROP_SQL = "DROP TABLE IF EXISTS " + TABLE_NAME;

	public static void onCreate(SQLiteDatabase db) {
		Log.w(LOG_TAG, CREATE_SQL);
		db.execSQL(CREATE_SQL);
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		Log.w(LOG_TAG, "Upgrading database from version " + oldVersion + " to "
				+ newVersion + ", which will destroy all old data");
		db.execSQL(DROP_SQL);
		onCreate(db);
	}
}
