package com.example.patientreminder;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class MyContentProvider extends ContentProvider {

	private DBHelper dbHelper;

	private static final int ALL_ENTITIES_1 = 1;
	private static final int SINGLE_ENTITY_1 = 2;
	private static final int ALL_ENTITIES_2 = 3;
	private static final int SINGLE_ENTITY_2 = 4;

	// authority is the symbolic name of your provider
	// To avoid conflicts with other providers, you should use
	// Internet domain ownership (in reverse) as the basis of your provider
	// authority.
	private static final String AUTHORITY = "com.example.patientreminder.contentprovider";

	// create content URIs from the authority by appending path to database
	// table
	public static final Uri CONTENT_URI_USER_CATALOG = Uri.parse("content://"
			+ AUTHORITY + "/entities1");
	public static final Uri CONTENT_URI_HISTORY = Uri.parse("content://"
			+ AUTHORITY + "/entities2");

	// a content URI pattern matches content URIs using wildcard characters:
	// *: Matches a string of any valid characters of any length.
	// #: Matches a string of numeric characters of any length.
	private static final UriMatcher uriMatcher;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(AUTHORITY, "entities1", ALL_ENTITIES_1);
		uriMatcher.addURI(AUTHORITY, "entities1/#", SINGLE_ENTITY_1);
		uriMatcher.addURI(AUTHORITY, "entities2", ALL_ENTITIES_2);
		uriMatcher.addURI(AUTHORITY, "entities2/#", SINGLE_ENTITY_2);
	}

	// system calls onCreate() when it starts up the provider.
	@Override
	public boolean onCreate() {
		// get access to the database helper
		dbHelper = new DBHelper(getContext());
		return false;
	}

	// Return the MIME type corresponding to a content URI
	@Override
	public String getType(Uri uri) {

		switch (uriMatcher.match(uri)) {
		case ALL_ENTITIES_1:
			return "vnd.android.cursor.dir/vnd.com.example.patientreminder.contentprovider.entities1";
		case SINGLE_ENTITY_1:
			return "vnd.android.cursor.item/vnd.com.example.patientreminder.contentprovider.entities1";
		case ALL_ENTITIES_2:
			return "vnd.android.cursor.dir/vnd.com.example.patientreminder.contentprovider.entities2";
		case SINGLE_ENTITY_2:
			return "vnd.android.cursor.item/vnd.com.example.patientreminder.contentprovider.entities2";
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}

	// The insert() method adds a new row to the appropriate table, using the
	// values
	// in the ContentValues argument. If a column name is not in the
	// ContentValues argument,
	// you may want to provide a default value for it either in your provider
	// code or in
	// your database schema.
	@Override
	public Uri insert(Uri uri, ContentValues values) {

		long id = 0;
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		switch (uriMatcher.match(uri)) {
		case ALL_ENTITIES_1:
			id = db.insert(UserCatalogDB.TABLE_NAME, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return Uri.parse(CONTENT_URI_USER_CATALOG + "/" + id);
		case ALL_ENTITIES_2:
			id = db.insert(HistoryDB.TABLE_NAME, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return Uri.parse(CONTENT_URI_HISTORY + "/" + id);
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}

	// The query() method must return a Cursor object, or if it fails,
	// throw an Exception. If you are using an SQLite database as your data
	// storage,
	// you can simply return the Cursor returned by one of the query() methods
	// of the
	// SQLiteDatabase class. If the query does not match any rows, you should
	// return a
	// Cursor instance whose getCount() method returns 0. You should return null
	// only
	// if an internal error occurred during the query process.
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		Cursor cursor = null;
		String id = null;
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

		switch (uriMatcher.match(uri)) {
		case ALL_ENTITIES_1:
			queryBuilder.setTables(UserCatalogDB.TABLE_NAME);
			cursor = queryBuilder.query(db, projection, selection,
					selectionArgs, null, null, sortOrder);
			return cursor;
		case SINGLE_ENTITY_1:
			queryBuilder.setTables(UserCatalogDB.TABLE_NAME);
			id = uri.getPathSegments().get(1);
			queryBuilder.appendWhere(UserCatalogDB._ID + "=" + id);
			cursor = queryBuilder.query(db, projection, selection,
					selectionArgs, null, null, sortOrder);
			return cursor;
		case ALL_ENTITIES_2:
			queryBuilder.setTables(HistoryDB.TABLE_NAME);
			cursor = queryBuilder.query(db, projection, selection,
					selectionArgs, null, null, sortOrder);
			return cursor;
		case SINGLE_ENTITY_2:
			queryBuilder.setTables(HistoryDB.TABLE_NAME);
			id = uri.getPathSegments().get(1);
			queryBuilder.appendWhere(UserCatalogDB._ID + "=" + id);
			cursor = queryBuilder.query(db, projection, selection,
					selectionArgs, null, null, sortOrder);
			return cursor;
		default:
			//throw new IllegalArgumentException("Unsupported URI: " + uri);
			return null;
		}

	}

	// The delete() method deletes rows based on the seletion or if an id is
	// provided then it deleted a single row. The methods returns the numbers
	// of records delete from the database. If you choose not to delete the data
	// physically then just update a flag here.
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {

		int deleteCount = 0;
		String id = null;
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		switch (uriMatcher.match(uri)) {
		case ALL_ENTITIES_1:
			deleteCount = db.delete(UserCatalogDB.TABLE_NAME, selection,
					selectionArgs);
			getContext().getContentResolver().notifyChange(uri, null);
			return deleteCount;
		case SINGLE_ENTITY_1:
			id = uri.getPathSegments().get(1);
			selection = UserCatalogDB._ID
					+ "="
					+ id
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
							+ ')' : "");
			deleteCount = db.delete(UserCatalogDB.TABLE_NAME, selection,
					selectionArgs);
			getContext().getContentResolver().notifyChange(uri, null);
			return deleteCount;
		case ALL_ENTITIES_2:
			deleteCount = db.delete(HistoryDB.TABLE_NAME, selection,
					selectionArgs);
			getContext().getContentResolver().notifyChange(uri, null);
			return deleteCount;
		case SINGLE_ENTITY_2:
			id = uri.getPathSegments().get(1);
			selection = HistoryDB._ID
					+ "="
					+ id
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
							+ ')' : "");
			deleteCount = db.delete(HistoryDB.TABLE_NAME, selection,
					selectionArgs);
			getContext().getContentResolver().notifyChange(uri, null);
			return deleteCount;
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}

	// The update method() is same as delete() which updates multiple rows
	// based on the selection or a single row if the row id is provided. The
	// update method returns the number of updated rows.
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {

		int updateCount = 0;
		String id = null;
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		switch (uriMatcher.match(uri)) {
		case ALL_ENTITIES_1:
			updateCount = db.update(UserCatalogDB.TABLE_NAME, values,
					selection, selectionArgs);
			getContext().getContentResolver().notifyChange(uri, null);
			return updateCount;
		case SINGLE_ENTITY_1:
			id = uri.getPathSegments().get(1);
			selection = UserCatalogDB._ID
					+ "="
					+ id
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
							+ ')' : "");
			updateCount = db.update(UserCatalogDB.TABLE_NAME, values,
					selection, selectionArgs);
			getContext().getContentResolver().notifyChange(uri, null);
			return updateCount;
		case ALL_ENTITIES_2:
			updateCount = db.update(HistoryDB.TABLE_NAME, values,
					selection, selectionArgs);
			getContext().getContentResolver().notifyChange(uri, null);
			return updateCount;
		case SINGLE_ENTITY_2:
			id = uri.getPathSegments().get(1);
			selection = HistoryDB._ID
					+ "="
					+ id
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
							+ ')' : "");
			updateCount = db.update(HistoryDB.TABLE_NAME, values,
					selection, selectionArgs);
			getContext().getContentResolver().notifyChange(uri, null);
			return updateCount;
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}

}