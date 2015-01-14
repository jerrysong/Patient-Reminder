package com.example.patientreminder;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

public class UserCatalogDB implements BaseColumns {

	public static final String TABLE_NAME = "userCatalog";
	public static final String LOG_TAG = "catalogDB";

	public static final String KEY_NAME = "name";
	public static final String KEY_SEX = "sex";
	public static final String KEY_AGE = "age";
	public static final String KEY_HEIGHT = "height";
	public static final String KEY_WEIGHT = "weight";
	public static final String KEY_NOTES = "notes";
	public static final String KEY_USER_REGISTER_ROW = "userRegisterRow";
	public static final String KEY_MEDICINE_NAME = "medicineName";
	public static final String KEY_DOSE = "dose";
	public static final String KEY_REASON = "reason";
	public static final String KEY_DOCTOR = "doctor";
	public static final String KEY_IMAGE = "image";
	public static final String KEY_TIME = "time";
	public static final String KEY_TIME_ZONE = "timeZone";
	public static final String KEY_START_DATE = "startDate";
	public static final String KEY_END_DATE = "endDate";
	public static final String KEY_REPEAT_FREQUENCY = "repeatFrequency";

	public static final String CREATE_SQL = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" 
	        + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ KEY_NAME + ","
			+ KEY_SEX + "," 
			+ KEY_AGE + "," 
			+ KEY_HEIGHT + "," 
			+ KEY_WEIGHT + "," 
			+ KEY_NOTES + "," 
			+ KEY_USER_REGISTER_ROW + ","
			+ KEY_MEDICINE_NAME + "," 
			+ KEY_DOSE + "," 
			+ KEY_REASON + "," 
			+ KEY_DOCTOR + "," 
			+ KEY_IMAGE + ","
			+ KEY_TIME + "," 
			+ KEY_TIME_ZONE + "," 
			+ KEY_START_DATE + ","
			+ KEY_END_DATE + ","
			+ KEY_REPEAT_FREQUENCY + ")";

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
