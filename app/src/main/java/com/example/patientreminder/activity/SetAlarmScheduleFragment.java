package com.example.patientreminder.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.patientreminder.AlarmReceiver;
import com.example.patientreminder.AlarmScheduler;
import com.example.patientreminder.Conveter;
import com.example.patientreminder.MyContentProvider;
import com.example.patientreminder.OrderAdapter;
import com.example.patientreminder.R;
import com.example.patientreminder.ReminderModel;
import com.example.patientreminder.UserCatalogDB;

public class SetAlarmScheduleFragment extends ListFragment {
	private Button btn_pick_start_time = null;
	private Button btn_pick_end_time = null;
	private ImageView btn_add_reminder = null;
	private Spinner repeat_selector = null;

	private ArrayList<ReminderModel> alarmList = null;
	private OrderAdapter adapter = null;
	private String userName = null;
	private String medicineName = null;
	private String medicineDose = null;
	private String doctor = null;
	private String reason = null;
	private String imageBitmapString = null;
	private String repeatSchedule = null;
	private String dateType = null;
	private GregorianCalendar startDate = null;
	private GregorianCalendar endDate = null;

	private final static String StartDate = "StartDate";
	private final static String EndDate = "EndDate";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_set_alarm_schedule,
				container, false);
	}

	@Override
	public void onStart() {
		super.onStart();
		importViews();
		initSpinner();
		readTempContent();

		alarmList = new ArrayList<ReminderModel>();
		adapter = new OrderAdapter(getActivity(), R.layout.listview_alarm_row,
				alarmList);
		setListAdapter(this.adapter);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.add_prescription, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_save:
			savePrescription();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void showDatePickerDialog() {
		Calendar c = Calendar.getInstance();
		int currentYear = c.get(Calendar.YEAR);
		int currentMonth = c.get(Calendar.MONTH);
		int currentDay = c.get(Calendar.DAY_OF_MONTH);

		DatePickerDialog date_picker_dialog = new DatePickerDialog(
				getActivity(), dateSetListener, currentYear, currentMonth,
				currentDay);
		date_picker_dialog.show();
	}

	private OnClickListener pickTimeClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (startDate == null || endDate == null) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						getActivity());
				builder.setMessage(
						"Please specify both the start date and the end date.")
						.setPositiveButton("OK", null);
				AlertDialog dialog = builder.show();
				TextView messageText = (TextView) dialog
						.findViewById(android.R.id.message);
				messageText.setGravity(Gravity.CENTER);
				dialog.show();
				return;
			} else if (startDate.getTimeInMillis() > endDate.getTimeInMillis()) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						getActivity());
				builder.setMessage(
						"The start date must be greater than the end date.")
						.setPositiveButton("OK", null);
				AlertDialog dialog = builder.show();
				TextView messageText = (TextView) dialog
						.findViewById(android.R.id.message);
				messageText.setGravity(Gravity.CENTER);
				dialog.show();
				return;
			}
			Calendar c = Calendar.getInstance();
			int currentHour = c.get(Calendar.HOUR_OF_DAY);
			int currentMinute = c.get(Calendar.MINUTE);

			TimePickerDialog time_picker_dialog = new TimePickerDialog(
					getActivity(), timeSetListener, currentHour, currentMinute,
					false);
			time_picker_dialog.show();
		}
	};

	private OnClickListener pickStartDateClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			dateType = StartDate;
			showDatePickerDialog();
		}
	};

	private OnClickListener pickEndDateClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (startDate == null) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						getActivity());
				builder.setMessage("Please specify the start date.")
						.setPositiveButton("OK", null);
				AlertDialog dialog = builder.show();
				TextView messageText = (TextView) dialog
						.findViewById(android.R.id.message);
				messageText.setGravity(Gravity.CENTER);
				dialog.show();
				return;
			}
			dateType = EndDate;
			showDatePickerDialog();
		}
	};

	private TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			// TODO Auto-generated method stub

			boolean isExisting = false;
			ReminderModel alarmTime = new ReminderModel(hourOfDay, minute,
					startDate, endDate, medicineName, medicineDose,
					imageBitmapString);
			for (ReminderModel existingTime : alarmList) {
				if (alarmTime.isEqual(existingTime)) {
					isExisting = true;
					break;
				}
			}
			if (!isExisting) {
				alarmList.add(alarmTime);
				adapter.notifyDataSetChanged();
			}
		}
	};

	private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			if (dateType.equals(StartDate)) {
				startDate = new GregorianCalendar(year, monthOfYear,
						dayOfMonth, 0, 0);
				btn_pick_start_time.setText(Conveter.getStringDate(startDate));
			} else if (dateType.equals(EndDate)) {
				endDate = new GregorianCalendar(year, monthOfYear, dayOfMonth,
						23, 59);
				btn_pick_end_time.setText(Conveter.getStringDate(endDate));
			} else {
				return;
			}
		}
	};

	private void savePrescription() {
		Intent returnIntent = new Intent();
		repeatSchedule = repeat_selector.getSelectedItem().toString();

		if (!alarmList.isEmpty() && !medicineName.isEmpty()) {
			for (ReminderModel alarmTime : alarmList) {
				Uri uri = saveToDatabase(alarmTime);
				launchAlarmService(alarmTime, uri);
			}
			getActivity().setResult(FragmentActivity.RESULT_OK, returnIntent);
			getActivity().finish();
		} else if (medicineName.isEmpty()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage("Please specify the medicine name")
					.setPositiveButton("OK", null).show();
		} else if (alarmList.isEmpty()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage("Please specify one reminder")
					.setPositiveButton("OK", null).show();
		} else {
			getActivity().setResult(FragmentActivity.RESULT_CANCELED,
					returnIntent);
			getActivity().finish();
		}
	}

	private void readTempContent() {
		SharedPreferences sharedPref = getActivity().getSharedPreferences(
				"AddPrescriptionActivity", Context.MODE_PRIVATE);
		userName = sharedPref.getString("userName", "");
		medicineName = sharedPref.getString("medicineName", "");
		medicineDose = sharedPref.getString("medicineDose", "")
				+ sharedPref.getString("doseUnit", "");
		reason = sharedPref.getString("reason", "");
		doctor = sharedPref.getString("doctor", "");
		imageBitmapString = sharedPref.getString("image", "");
	}

	private void importViews() {
		btn_pick_start_time = (Button) getActivity().findViewById(
				R.id.btn_pick_start_time);
		btn_pick_start_time.setOnClickListener(pickStartDateClickListener);

		btn_pick_end_time = (Button) getActivity().findViewById(
				R.id.btn_pick_end_time);
		btn_pick_end_time.setOnClickListener(pickEndDateClickListener);

		btn_add_reminder = (ImageView) getActivity().findViewById(
				R.id.btn_add_reminder);
		btn_add_reminder.setOnClickListener(pickTimeClickListener);

		repeat_selector = (Spinner) getActivity().findViewById(
				R.id.spinner_repeat_selector);
	}

	private void initSpinner() {
		ArrayAdapter<CharSequence> repeatAdapter = ArrayAdapter
				.createFromResource(getActivity(),
						R.array.repeat_schedule_array, R.layout.spinner_view);
		repeatAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		repeat_selector.setAdapter(repeatAdapter);
	}

	private void launchAlarmService(ReminderModel alarmTime, Uri uri) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, alarmTime.hourOfDay);
		calendar.set(Calendar.MINUTE, alarmTime.minute);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		Context context = getActivity();
		Calendar scheduledCalendar = AlarmScheduler.getScheduledCalendar(
				calendar, repeatSchedule, context);

		boolean validation = AlarmScheduler.validateCalendar(scheduledCalendar,
				repeatSchedule, startDate, endDate, context);
		if (!validation) {
			return;
		}

		Intent intent = new Intent(AlarmReceiver.ACTION_ALARM, uri);
		intent.setClass(getActivity(), AlarmReceiver.class);
		intent.putExtra("Uri", uri.toString());

		PendingIntent pending = PendingIntent.getBroadcast(getActivity(), 0,
				intent, 0);
		AlarmManager alarm = (AlarmManager) getActivity().getSystemService(
				Context.ALARM_SERVICE);

		alarm.set(AlarmManager.RTC_WAKEUP, scheduledCalendar.getTimeInMillis(),
				pending);

//		Toast.makeText(
//				context,
//				String.valueOf(scheduledCalendar.get(Calendar.YEAR) + " "
//						+ scheduledCalendar.get(Calendar.MONTH) + " "
//						+ scheduledCalendar.get(Calendar.DAY_OF_MONTH) + " "
//						+ scheduledCalendar.get(Calendar.HOUR_OF_DAY) + " "
//						+ scheduledCalendar.get(Calendar.MINUTE)),
//				Toast.LENGTH_LONG).show();
	}

	private Uri saveToDatabase(ReminderModel alarmTime) {
		ContentValues values = new ContentValues();
		values.put(UserCatalogDB.KEY_USER_REGISTER_ROW, "false");
		values.put(UserCatalogDB.KEY_NAME, userName);
		values.put(UserCatalogDB.KEY_MEDICINE_NAME, medicineName);
		values.put(UserCatalogDB.KEY_DOSE, medicineDose);
		values.put(UserCatalogDB.KEY_REASON, reason);
		values.put(UserCatalogDB.KEY_DOCTOR, doctor);
		values.put(UserCatalogDB.KEY_IMAGE, imageBitmapString);

		String startDateString = Conveter.getStringDate(startDate);
		values.put(UserCatalogDB.KEY_START_DATE, startDateString);

		String endDateString = Conveter.getStringDate(endDate);
		values.put(UserCatalogDB.KEY_END_DATE, endDateString);

		String alarmTimeZoneString = Conveter.getSavedTimeZone(alarmTime);
		values.put(UserCatalogDB.KEY_TIME_ZONE, alarmTimeZoneString);

		String alarmTimeString = Conveter.getSavedTimeValue(alarmTime);
		values.put(UserCatalogDB.KEY_TIME, alarmTimeString);

		values.put(UserCatalogDB.KEY_REPEAT_FREQUENCY, repeatSchedule);
		return getActivity().getContentResolver().insert(
				MyContentProvider.CONTENT_URI_USER_CATALOG, values);
	}

}