package com.example.patientreminder.activity;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.example.patientreminder.MyContentProvider;
import com.example.patientreminder.R;
import com.example.patientreminder.UserCatalogDB;

public class ShowPatientsPanelActivity extends Activity implements
		LoaderManager.LoaderCallbacks<Cursor> {
	private final static int REG_NEW_PATIENT_CODE = 1;
	private final static int SHOW_PRESCRIPTIONS_PANEL_CODE = 2;
	private SimpleCursorAdapter dataAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_patients_panel);
		displayPatientsInfo();
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
		getMenuInflater().inflate(R.menu.show_patients_panel, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.action_add:
			addPatient();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REG_NEW_PATIENT_CODE) {
			if (resultCode == RESULT_OK) {
				displayPatientsInfo();
			}
			if (resultCode == RESULT_CANCELED) {
				// Write your code if there's no result
			}
		}
	}

	// This is called when a new Loader needs to be created.
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] projection = { UserCatalogDB._ID, UserCatalogDB.KEY_NAME };
		String select = UserCatalogDB.KEY_USER_REGISTER_ROW + "='true'";
		String orderBy = UserCatalogDB.KEY_NAME + " ASC";
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

	public void addPatient() {
		Intent intent = new Intent(this,
				RegisterOrEditPatientInfoActivity.class);
		intent.putExtra("Target", "add");
		startActivityForResult(intent, REG_NEW_PATIENT_CODE);
	}

	private void displayPatientsInfo() {
		// The desired columns to be bound
		String[] columns = new String[] { UserCatalogDB.KEY_NAME };

		// the XML defined views which the data will be bound to
		int[] to = new int[] { R.id.patient_name };

		// create an adapter from the SimpleCursorAdapter
		dataAdapter = new SimpleCursorAdapter(this,
				R.layout.listview_patients_row, null, columns, to, 0);

		// get reference to the ListView
		ListView listView = (ListView) findViewById(R.id.patient_list);
		// Assign adapter to ListView
		listView.setAdapter(dataAdapter);
		// Ensures a loader is initialized and active.
		getLoaderManager().initLoader(0, null, this);

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> listView, View view,
					int position, long id) {
				// Get the cursor, positioned to the corresponding row in the
				// result set
				Cursor cursor = (Cursor) listView.getItemAtPosition(position);
				String userName = cursor.getString(cursor
						.getColumnIndexOrThrow(UserCatalogDB.KEY_NAME));

				Intent intent = new Intent(ShowPatientsPanelActivity.this,
						ShowPrescriptionsPanelActivity.class);
				intent.putExtra("UserName", userName);
				startActivityForResult(intent, SHOW_PRESCRIPTIONS_PANEL_CODE);
			}
		});
	}
}
