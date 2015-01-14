package com.example.patientreminder.activity;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ListView;
import android.widget.TextView;

import com.example.patientreminder.HistoryDB;
import com.example.patientreminder.HistoryRowCursorAdapter;
import com.example.patientreminder.MyContentProvider;
import com.example.patientreminder.R;

public class BrowseHistoryActivity extends Activity implements
		LoaderManager.LoaderCallbacks<Cursor> {

	private TextView mTitle = null;
	private HistoryRowCursorAdapter dataAdapter = null;
	private String userName = null;
	private String calendarString = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		userName = intent.getStringExtra("UserName");
		calendarString = intent.getStringExtra("Date");
		setContentView(R.layout.activity_browse_history);
		mTitle = (TextView) findViewById(R.id.text_calendar_current_time);
		mTitle.setText(calendarString);
		displayPatientDetails();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.browse_history, menu);
		return true;
	}

	// This is called when a new Loader needs to be created.
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] projection = { HistoryDB._ID, HistoryDB.KEY_MEDICINE,
				HistoryDB.KEY_ACTUAL_TIME, HistoryDB.KEY_ACTUAL_TIME_ZONE,
				HistoryDB.KEY_SCHEDULED_TIME,
				HistoryDB.KEY_SCHEDULED_TIME_ZONE, HistoryDB.KEY_DOSE,
				HistoryDB.KEY_IMAGE, HistoryDB.KEY_COMPLETITION };
		String select = "(" + HistoryDB.KEY_USER + "='" + this.userName
				+ "') AND (" + HistoryDB.KEY_DATE + "='" + this.calendarString
				+ "')";
		String orderBy = HistoryDB.KEY_ACTUAL_TIME_ZONE + " ASC,"
				+ HistoryDB.KEY_ACTUAL_TIME + " ASC";
		CursorLoader cursorLoader = new CursorLoader(this,
				MyContentProvider.CONTENT_URI_HISTORY, projection, select,
				null, orderBy);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// Swap the new cursor in. (The framework will take care of closing the
		// old cursor once we return.)
		dataAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// This is called when the last Cursor provided to onLoadFinished()
		// above is about to be closed. We need to make sure we are no
		// longer using it.
		dataAdapter.swapCursor(null);
	}

	private void displayPatientDetails() {
		// The desired columns to be bound
		String[] columns = new String[] { HistoryDB.KEY_MEDICINE,
				HistoryDB.KEY_DOSE, HistoryDB.KEY_IMAGE };

		// the XML defined views which the data will be bound to
		int[] to = new int[] { R.id.history_text_medicine_name,
				R.id.history_text_medicine_dose,
				R.id.history_image_medicine_icon };

		// create an adapter from the SimpleCursorAdapter
		dataAdapter = new HistoryRowCursorAdapter(BrowseHistoryActivity.this,
				R.layout.listview_history_row, null, columns, to, 0);

		// get reference to the ListView
		ListView listView = (ListView) findViewById(R.id.history_list);
		// Assign adapter to ListView
		listView.setAdapter(dataAdapter);
		// Ensures a loader is initialized and active.
		getLoaderManager().initLoader(0, null, this);
	}
}
