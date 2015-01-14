package com.example.patientreminder.activity;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.patientreminder.AlarmRowCursorAdapter;
import com.example.patientreminder.MyContentProvider;
import com.example.patientreminder.R;
import com.example.patientreminder.UserCatalogDB;

public class ShowPrescriptionsPanelActivity extends Activity implements
		LoaderManager.LoaderCallbacks<Cursor> {
	private final static int ADD_REMINDER_CODE = 1;
	private final static int UPDATE_PATIENT_INFO_CODE = 2;
	private final static int BROWSE_HISTORY = 3;
	private AlarmRowCursorAdapter dataAdapter = null;
	private String userName = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_prescriptions_panel);

		Intent intent = getIntent();
		userName = intent.getStringExtra("UserName");
		setTitle(userName);

		displayPatientDetails();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Starts a new or restarts an existing Loader in this manager
		getLoaderManager().restartLoader(0, null, this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_prescriptions_panel, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_add:
			addNewReminder();
			break;
		case R.id.action_update:
			updatePatientInfo();
			break;
		case R.id.action_browse_history:
			browseHistory();
			break;
		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	// This is called when a new Loader needs to be created.
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] projection = { UserCatalogDB._ID,
				UserCatalogDB.KEY_MEDICINE_NAME, UserCatalogDB.KEY_TIME_ZONE,
				UserCatalogDB.KEY_TIME, UserCatalogDB.KEY_DOSE,
				UserCatalogDB.KEY_IMAGE, UserCatalogDB.KEY_START_DATE,
				UserCatalogDB.KEY_END_DATE };
		String select = "(" + UserCatalogDB.KEY_NAME + "='" + this.userName
				+ "') AND (" + UserCatalogDB.KEY_USER_REGISTER_ROW
				+ "='false')";
		String orderBy = UserCatalogDB.KEY_TIME_ZONE + " ASC,"
				+ UserCatalogDB.KEY_TIME + " ASC";
		CursorLoader cursorLoader = new CursorLoader(this,
				MyContentProvider.CONTENT_URI_USER_CATALOG, projection, select, null,
				orderBy);
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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case ADD_REMINDER_CODE:
			if (resultCode == Activity.RESULT_OK) {
				break;
			} else if (resultCode == Activity.RESULT_CANCELED) {
				break;
			}
		case UPDATE_PATIENT_INFO_CODE:
			if (resultCode == Activity.RESULT_OK) {
				Intent returnIntent = new Intent();
				setResult(RESULT_OK, returnIntent);
				finish();
				break;
			} else if (resultCode == Activity.RESULT_CANCELED) {
				break;
			}
		}
	}

	private void updatePatientInfo() {

		String[] projection = { UserCatalogDB._ID, UserCatalogDB.KEY_AGE,
				UserCatalogDB.KEY_SEX, UserCatalogDB.KEY_HEIGHT,
				UserCatalogDB.KEY_WEIGHT, UserCatalogDB.KEY_NOTES };
		String select = "(" + UserCatalogDB.KEY_USER_REGISTER_ROW + "='true')"
				+ " And " + "(" + UserCatalogDB.KEY_NAME + "='" + userName
				+ "')";
		Cursor cursor = getContentResolver().query(
				MyContentProvider.CONTENT_URI_USER_CATALOG, projection, select, null, null);

		int id = 0;
		String age = null;
		String sex = null;
		String height = null;
		String weight = null;
		String notes = null;
		if (cursor != null && cursor.moveToFirst()) {
			id = cursor.getInt(cursor.getColumnIndexOrThrow(UserCatalogDB._ID));
			age = cursor.getString(cursor
					.getColumnIndexOrThrow(UserCatalogDB.KEY_AGE));
			sex = cursor.getString(cursor
					.getColumnIndexOrThrow(UserCatalogDB.KEY_SEX));
			height = cursor.getString(cursor
					.getColumnIndexOrThrow(UserCatalogDB.KEY_HEIGHT));
			weight = cursor.getString(cursor
					.getColumnIndexOrThrow(UserCatalogDB.KEY_WEIGHT));
			notes = cursor.getString(cursor
					.getColumnIndexOrThrow(UserCatalogDB.KEY_NOTES));
		}

		Intent intent = new Intent(this,
				RegisterOrEditPatientInfoActivity.class);
		intent.putExtra("Target", "edit");
		intent.putExtra("ID", id);
		intent.putExtra("UserName", userName);
		intent.putExtra("Age", age);
		intent.putExtra("Sex", sex);
		intent.putExtra("Height", height);
		intent.putExtra("Weight", weight);
		intent.putExtra("Notes", notes);
		startActivityForResult(intent, UPDATE_PATIENT_INFO_CODE);
	}

	private void addNewReminder() {
		Intent intent = new Intent(this, AddPrescriptionActivity.class);
		intent.putExtra("UserName", userName);
		startActivityForResult(intent, ADD_REMINDER_CODE);
	}
	
	private void browseHistory(){
		Intent intent = new Intent(this, CalendarActivity.class);
		intent.putExtra("UserName", userName);
		startActivityForResult(intent, BROWSE_HISTORY);
	}

	private void displayPatientDetails() {
		// The desired columns to be bound
		String[] columns = new String[] { UserCatalogDB.KEY_MEDICINE_NAME,
				UserCatalogDB.KEY_TIME_ZONE, UserCatalogDB.KEY_TIME,
				UserCatalogDB.KEY_DOSE, UserCatalogDB.KEY_IMAGE,
				UserCatalogDB.KEY_START_DATE, UserCatalogDB.KEY_END_DATE };

		// the XML defined views which the data will be bound to
		int[] to = new int[] { R.id.text_medicine_name, R.id.text_time_zone,
				R.id.text_time, R.id.text_medicine_dose, R.id.medicine_icon,
				R.id.text_start_date, R.id.text_end_date };

		// create an adapter from the SimpleCursorAdapter
		dataAdapter = new AlarmRowCursorAdapter(this,
				R.layout.listview_alarm_row, null, columns, to, 0);

		// get reference to the ListView
		ListView listView = (ListView) findViewById(R.id.reminder_list);
		// Assign adapter to ListView
		listView.setAdapter(dataAdapter);
		// Ensures a loader is initialized and active.
		getLoaderManager().initLoader(0, null, this);
	}
}
