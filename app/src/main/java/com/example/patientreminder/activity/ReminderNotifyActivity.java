package com.example.patientreminder.activity;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.patientreminder.Conveter;
import com.example.patientreminder.HistoryDB;
import com.example.patientreminder.MyContentProvider;
import com.example.patientreminder.PendingAlarmReceiver;
import com.example.patientreminder.R;
import com.example.patientreminder.UserCatalogDB;

public class ReminderNotifyActivity extends Activity {
	public final static String ACTION_NOTIFY = "ACTION_NOTIFY";
	private ImageView image_medicine_icon = null;
	private TextView text_medicine_name = null;
	private TextView text_medicine_dose = null;
	private Button btn_accept = null;
	private Button btn_remind_later = null;
	private Button btn_dismiss = null;
	private Uri uri = null;
	private String userName = null;
	private String medicineName = null;
	private String medicineDose = null;
	private String imageBitmapString = null;
	private String scheduledTime = null;
	private String scheduledTimeZone = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reminder_notify);
		uri = Uri.parse(getIntent().getStringExtra("Uri"));
		Cursor cursor = getContentResolver().query(uri, null, null, null, null);
		if (cursor.moveToFirst()) {
			importViews(cursor);
		}
		this.setFinishOnTouchOutside(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.reminder_notify, menu);
		return true;
	}

	private void importViews(Cursor cursor) {
		userName = cursor.getString(cursor
				.getColumnIndexOrThrow(UserCatalogDB.KEY_NAME));
		medicineName = cursor.getString(cursor
				.getColumnIndexOrThrow(UserCatalogDB.KEY_MEDICINE_NAME));
		medicineDose = cursor.getString(cursor
				.getColumnIndexOrThrow(UserCatalogDB.KEY_DOSE));
		imageBitmapString = cursor.getString(cursor
				.getColumnIndexOrThrow(UserCatalogDB.KEY_IMAGE));
		scheduledTime = cursor.getString(cursor
				.getColumnIndexOrThrow(UserCatalogDB.KEY_TIME));
		scheduledTimeZone = cursor.getString(cursor
				.getColumnIndexOrThrow(UserCatalogDB.KEY_TIME_ZONE));

		image_medicine_icon = (ImageView) findViewById(R.id.image_medicine_icon);
		text_medicine_name = (TextView) findViewById(R.id.text_medicine_name);
		text_medicine_dose = (TextView) findViewById(R.id.text_medicine_dose);
		btn_accept = (Button) findViewById(R.id.btn_accept);
		btn_remind_later = (Button) findViewById(R.id.btn_remind_later);
		btn_dismiss = (Button) findViewById(R.id.btn_dismiss);

		if (!imageBitmapString.isEmpty()) {
			image_medicine_icon.setImageBitmap(Conveter
					.convertStringToBitmap(imageBitmapString));
		} else {
			image_medicine_icon.setImageResource(R.drawable.medicine_icon);
		}
		text_medicine_name.setText(medicineName);
		text_medicine_dose.setText(medicineDose);

		btn_accept.setOnClickListener(acceptClickListener);
		btn_remind_later.setOnClickListener(remindLaterClickListener);
		btn_dismiss.setOnClickListener(dismissClickListener);
	}

	private String getCurrentTime() {
		StringBuilder time = new StringBuilder();
		Calendar c = Calendar.getInstance();

		String hours = String.valueOf(c.get(Calendar.HOUR));
		time.append(hours);

		String minutes = String.valueOf(c.get(Calendar.MINUTE));
		if (minutes.length() == 1) {
			time.append(":0" + minutes);
		} else {
			time.append(":" + minutes);
		}

		return time.toString();
	}

	private String getCurrentTimeZone() {
		String timeZone = null;
		Calendar c = Calendar.getInstance();
		int hourOfDay = c.get(Calendar.HOUR_OF_DAY);
		if (hourOfDay < 12) {
			timeZone = "AM";
		} else {
			timeZone = "PM";
		}

		return timeZone;
	}

	private void saveToDatabase(String completition) {
		GregorianCalendar calendar = new GregorianCalendar();
		String calendarString = Conveter.getStringDate(calendar);
		String time = getCurrentTime();
		String timeZone = getCurrentTimeZone();

		ContentValues values = new ContentValues();
		values.put(HistoryDB.KEY_DATE, calendarString);
		values.put(HistoryDB.KEY_USER, userName);
		values.put(HistoryDB.KEY_MEDICINE, medicineName);
		values.put(HistoryDB.KEY_DOSE, medicineDose);
		values.put(HistoryDB.KEY_IMAGE, imageBitmapString);
		values.put(HistoryDB.KEY_ACTUAL_TIME, time);
		values.put(HistoryDB.KEY_ACTUAL_TIME_ZONE, timeZone);
		values.put(HistoryDB.KEY_COMPLETITION, completition);
		values.put(HistoryDB.KEY_SCHEDULED_TIME, scheduledTime);
		values.put(HistoryDB.KEY_SCHEDULED_TIME_ZONE, scheduledTimeZone);

		getContentResolver().insert(MyContentProvider.CONTENT_URI_HISTORY,
				values);
	}

	private OnClickListener acceptClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			String completition = "true";
			saveToDatabase(completition);
			finish();
		}
	};

	private OnClickListener remindLaterClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			calendar.add(Calendar.MINUTE, 5);

			Intent intent = new Intent(PendingAlarmReceiver.PENDING_ALARM, uri);
			intent.setClass(ReminderNotifyActivity.this,
					PendingAlarmReceiver.class);
			intent.putExtra("Uri", uri.toString());

			PendingIntent pending = PendingIntent.getBroadcast(
					getBaseContext(), 0, intent, 0);
			AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

			alarm.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
					pending);

			finish();
		}
	};

	private OnClickListener dismissClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			String completition = "false";
			saveToDatabase(completition);
			finish();
		}
	};

}
