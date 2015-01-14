package com.example.patientreminder;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	private final static String DATABASE_NAME = "mydatabase.db";
	private final static int DATABASE_VERSION = 17;

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		UserCatalogDB.onCreate(db);
		HistoryDB.onCreate(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		UserCatalogDB.onUpgrade(db, oldVersion, newVersion);
		HistoryDB.onUpgrade(db, oldVersion, newVersion);
	}
}
