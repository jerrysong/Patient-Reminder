package com.example.patientreminder.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.patientreminder.MyContentProvider;
import com.example.patientreminder.R;
import com.example.patientreminder.UserCatalogDB;

public class RegisterOrEditPatientInfoActivity extends Activity {

	private EditText edit_patient_first_name = null;
	private EditText edit_patient_last_name = null;
	private EditText edit_patient_age = null;
	private EditText edit_patient_height = null;
	private EditText edit_patient_weight = null;
	private EditText edit_patient_notes = null;
	private Button confirm_button = null;
	private Button delete_button = null;
	private Spinner sex_selector = null;
	private Spinner weight_selector = null;
	private Spinner height_selector = null;

	private int id = 0;
	private String target = null;
	private String userName = null;
	private String age = null;
	private String sex = null;
	private String height = null;
	private String weight = null;
	private String notes = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_or_edit_patient_info);
		// Show the Up button in the action bar.
		setupActionBar();
		// Import the view components from the layout
		importView();
		initSpinner();

		Intent intent = getIntent();
		target = intent.getStringExtra("Target");
		if (target.equals("edit")) {
			editModeInit(intent);
		} else if (target.equals("add")) {
			addModeInit(intent);
		}
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register_new_patient, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {

		View v = getCurrentFocus();
		boolean ret = super.dispatchTouchEvent(event);

		if (v instanceof EditText) {
			View w = getCurrentFocus();
			int scrcoords[] = new int[2];
			w.getLocationOnScreen(scrcoords);
			float x = event.getRawX() + w.getLeft() - scrcoords[0];
			float y = event.getRawY() + w.getTop() - scrcoords[1];

			Log.d("Activity",
					"Touch event " + event.getRawX() + "," + event.getRawY()
							+ " " + x + "," + y + " rect " + w.getLeft() + ","
							+ w.getTop() + "," + w.getRight() + ","
							+ w.getBottom() + " coords " + scrcoords[0] + ","
							+ scrcoords[1]);
			if (event.getAction() == MotionEvent.ACTION_UP
					&& (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w
							.getBottom())) {

				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(getWindow().getCurrentFocus()
						.getWindowToken(), 0);
			}
		}
		return ret;
	}
	
	View.OnClickListener cancelNewPatient = new View.OnClickListener() {
		public void onClick(View v) {
			finish();
		}
	};

	View.OnClickListener deletePatientInfo = new View.OnClickListener() {
		public void onClick(View v) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					RegisterOrEditPatientInfoActivity.this);
			builder.setMessage("Are you sure to delete this user account?")
					.setPositiveButton("Yes", deleteDialogClickListener)
					.setNegativeButton("No", deleteDialogClickListener).show();
		}
	};

	View.OnClickListener confirmNewPatient = new View.OnClickListener() {
		public void onClick(View v) {
			ContentValues values = new ContentValues();
			values = getValuesFromUI();
			if (values == null) {
				return;
			}

			getContentResolver().insert(MyContentProvider.CONTENT_URI_USER_CATALOG, values);

			Intent returnIntent = new Intent();
			setResult(RESULT_OK, returnIntent);
			finish();
		}
	};

	View.OnClickListener editPatientInfo = new View.OnClickListener() {
		public void onClick(View v) {
			ContentValues values = new ContentValues();
			values = getValuesFromUI();
			if (values == null) {
				return;
			}

			Uri uri = Uri.parse(MyContentProvider.CONTENT_URI_USER_CATALOG + "/" + id);
			getContentResolver().update(uri, values, null, null);

			Intent returnIntent = new Intent();
			setResult(RESULT_OK, returnIntent);
			finish();
		}
	};

	DialogInterface.OnClickListener deleteDialogClickListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				String[] projection = { UserCatalogDB._ID };
				String select = "(" + UserCatalogDB.KEY_NAME + "='" + userName
						+ "')";
				Cursor cursor = getContentResolver().query(
						MyContentProvider.CONTENT_URI_USER_CATALOG, projection, select,
						null, null);

				while (cursor != null && cursor.moveToNext()) {
					int deleteID = cursor.getInt(cursor
							.getColumnIndexOrThrow(UserCatalogDB._ID));
					Uri uri = Uri.parse(MyContentProvider.CONTENT_URI_USER_CATALOG + "/"
							+ deleteID);
					getContentResolver().delete(uri, null, null);
				}

				Intent returnIntent = new Intent();
				setResult(RESULT_OK, returnIntent);
				finish();
				break;

			case DialogInterface.BUTTON_NEGATIVE:
				return;
			}
		}
	};

	private void editModeInit(Intent intent) {
		id = intent.getIntExtra("ID", 0);
		userName = intent.getStringExtra("UserName");
		age = intent.getStringExtra("Age");
		sex = intent.getStringExtra("Sex");
		height = intent.getStringExtra("Height");
		weight = intent.getStringExtra("Weight");
		notes = intent.getStringExtra("Notes");

		edit_patient_age.setText(age);
		edit_patient_notes.setText(notes);

		String[] splitUserName = userName.split(" ");
		edit_patient_first_name.setText(splitUserName[0]);
		if (splitUserName.length > 1) {
			edit_patient_last_name.setText(splitUserName[1]);
		}
		edit_patient_first_name.setKeyListener(null);
		edit_patient_last_name.setKeyListener(null);

		if (sex.equals("Male")) {
			sex_selector.setSelection(1);
		} else if (sex.equals("Female")) {
			sex_selector.setSelection(2);
		} else {
			sex_selector.setSelection(0);
		}

		String[] splitHeight = height.split(" ");
		edit_patient_height.setText(splitHeight[0]);
		if (splitHeight[1].equals("inches")) {
			height_selector.setSelection(0);
		} else if (splitHeight[1].equals("ft")) {
			height_selector.setSelection(1);
		}

		String[] splitWeight = weight.split(" ");
		edit_patient_weight.setText(splitWeight[0]);
		if (splitWeight[1].equals("lb")) {
			weight_selector.setSelection(0);
		} else if (splitWeight[1].equals("kg")) {
			weight_selector.setSelection(1);
		}

		edit_patient_first_name.setKeyListener(null);
		edit_patient_last_name.setKeyListener(null);

		delete_button.setText(R.string.btn_delete_patient);
		delete_button.setOnClickListener(deletePatientInfo);
		confirm_button.setText(R.string.btn_update_patient);
		confirm_button.setOnClickListener(editPatientInfo);
	}

	private void addModeInit(Intent intent) {
		delete_button.setText(R.string.btn_cancel_new_patient);
		delete_button.setOnClickListener(cancelNewPatient);
		confirm_button.setText(R.string.btn_confirm_new_patient);
		confirm_button.setOnClickListener(confirmNewPatient);
	}

	private ContentValues getValuesFromUI() {
		userName = edit_patient_first_name.getText().toString() + " "
				+ edit_patient_last_name.getText().toString();
		age = edit_patient_age.getText().toString();
		sex = sex_selector.getSelectedItem().toString();
		height = edit_patient_height.getText().toString() + " "
				+ height_selector.getSelectedItem().toString();
		weight = edit_patient_weight.getText().toString() + " "
				+ weight_selector.getSelectedItem().toString();
		notes = edit_patient_notes.getText().toString();

		if (userName.trim().equalsIgnoreCase("")) {
			Toast.makeText(getBaseContext(), "Please ENTER user name",
					Toast.LENGTH_LONG).show();
			return null;
		}

		ContentValues values = new ContentValues();
		values.put(UserCatalogDB.KEY_USER_REGISTER_ROW, "true");
		values.put(UserCatalogDB.KEY_NAME, userName);
		values.put(UserCatalogDB.KEY_AGE, age);
		values.put(UserCatalogDB.KEY_SEX, sex);
		values.put(UserCatalogDB.KEY_HEIGHT, height);
		values.put(UserCatalogDB.KEY_WEIGHT, weight);
		values.put(UserCatalogDB.KEY_NOTES, notes);

		if (target.equals("add")) {
			if (checkDuplication(values)) {
				Toast.makeText(getBaseContext(),
						"This user account already exists", Toast.LENGTH_LONG)
						.show();
				return null;
			}
		}

		return values;
	}

	private Boolean checkDuplication(ContentValues values) {
		String[] projection = { UserCatalogDB.KEY_NAME };
		String select = UserCatalogDB.KEY_USER_REGISTER_ROW + "='true'";
		Cursor cursor = getContentResolver().query(
				MyContentProvider.CONTENT_URI_USER_CATALOG, projection, select, null, null);

		String existingUser = null;

		if (cursor != null) {
			while (cursor.moveToNext()) {
				existingUser = cursor.getString(cursor
						.getColumnIndexOrThrow(UserCatalogDB.KEY_NAME));

				if (existingUser.trim().equalsIgnoreCase(
						values.getAsString(UserCatalogDB.KEY_NAME))) {
					return true;
				}
			}
		}
		return false;
	}

	private void initSpinner() {
		ArrayAdapter<CharSequence> sexAdapter = ArrayAdapter
				.createFromResource(this, R.array.sex_array,
						R.layout.spinner_view);
		sexAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sex_selector.setAdapter(sexAdapter);

		ArrayAdapter<CharSequence> weightAdapter = ArrayAdapter
				.createFromResource(this, R.array.mass_unit_array,
						R.layout.spinner_view);
		weightAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		weight_selector.setAdapter(weightAdapter);

		ArrayAdapter<CharSequence> heightAdapter = ArrayAdapter
				.createFromResource(this, R.array.length_unit_array,
						R.layout.spinner_view);
		heightAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		height_selector.setAdapter(heightAdapter);
	}

	private void importView() {
		edit_patient_first_name = (EditText) findViewById(R.id.edit_patient_first_name);
		edit_patient_last_name = (EditText) findViewById(R.id.edit_patient_last_name);
		edit_patient_age = (EditText) findViewById(R.id.edit_patient_age);
		edit_patient_height = (EditText) findViewById(R.id.edit_patient_height);
		edit_patient_weight = (EditText) findViewById(R.id.edit_patient_weight);
		edit_patient_notes = (EditText) findViewById(R.id.edit_patient_notes);
		confirm_button = (Button) findViewById(R.id.btn_confirm);
		delete_button = (Button) findViewById(R.id.btn_delete);
		sex_selector = (Spinner) findViewById(R.id.spinner_sex_selector);
		weight_selector = (Spinner) findViewById(R.id.spinner_weight_selector);
		height_selector = (Spinner) findViewById(R.id.spinner_height_selector);
	}
}
