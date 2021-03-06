package com.example.patientreminder;

import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.widget.Toast;

import com.example.patientreminder.activity.ReminderNotifyActivity;

public class AlarmService extends Service {

	private final static int NOTIFICATION_ID = 0;
	private int hourOfDay = 0;
	private int minute = 0;
	private String repeatSchedule = null;
	private NotificationManager mManager = null;
	private GregorianCalendar startDate = null;
	private GregorianCalendar endDate = null;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		String uriString = intent.getStringExtra("Uri");
		Uri uri = Uri.parse(uriString);

		String contentText = null;
		Cursor cursor = getAlarmInfoCursor(uri);
		if (cursor == null) {
			return super.onStartCommand(intent, flags, startId);			
		} else {
			try {
				contentText = getNotificationContentText(cursor);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		Toast.makeText(getBaseContext(), "Prescription Reminder",
				Toast.LENGTH_LONG).show();

		mManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

		Intent notificationIntent = new Intent(
				ReminderNotifyActivity.ACTION_NOTIFY, uri);
		notificationIntent.setClass(this, ReminderNotifyActivity.class);
		notificationIntent.putExtra("Uri", uri.toString());

		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);

		long[] vibrationPattern = { 0, 2000, 0 };
		Notification notification = new Notification.Builder(this)
				.setContentTitle("Prescription Reminder")
				.setContentText(contentText).setSmallIcon(R.drawable.icon)
				.setVibrate(vibrationPattern).setContentIntent(pendingIntent)
				.build();
		notification.flags += Notification.FLAG_ONGOING_EVENT;
		notification.flags += Notification.FLAG_AUTO_CANCEL;

		mManager.notify(NOTIFICATION_ID, notification);

		launchNextReminderService(uri);

		return super.onStartCommand(intent, flags, startId);
	}

	private void launchNextReminderService(Uri uri) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		Calendar scheduledCalendar = AlarmScheduler.getScheduledCalendar(
				calendar, repeatSchedule, AlarmService.this);

		boolean validation = AlarmScheduler.validateCalendar(scheduledCalendar,
				repeatSchedule, startDate, endDate, this);
		if (!validation) {
			return;
		}

		Intent intent = new Intent(AlarmReceiver.ACTION_ALARM, uri);
		intent.setClass(this, AlarmReceiver.class);
		intent.putExtra("Uri", uri.toString());

		PendingIntent pending = PendingIntent.getBroadcast(getBaseContext(), 0,
				intent, 0);
		AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		alarm.set(AlarmManager.RTC_WAKEUP, scheduledCalendar.getTimeInMillis(),
				pending);
	}

	private Cursor getAlarmInfoCursor(Uri uri) {
		String[] projection = { UserCatalogDB.KEY_NAME,
				UserCatalogDB.KEY_MEDICINE_NAME, UserCatalogDB.KEY_DOSE,
				UserCatalogDB.KEY_TIME, UserCatalogDB.KEY_TIME_ZONE,
				UserCatalogDB.KEY_REPEAT_FREQUENCY,
				UserCatalogDB.KEY_START_DATE, UserCatalogDB.KEY_END_DATE };
		Cursor cursor = getContentResolver().query(uri, projection, null, null,
				null);

		if (cursor == null || cursor.moveToFirst() == false) {
			return null;
		} else {
			return cursor;
		}
	}

	private String getNotificationContentText(Cursor cursor)
			throws ParseException {
		if (cursor == null) {
			return null;
		}

		String contentText = null;
		String userName = cursor.getString(cursor
				.getColumnIndexOrThrow(UserCatalogDB.KEY_NAME));
		String medicineName = cursor.getString(cursor
				.getColumnIndexOrThrow(UserCatalogDB.KEY_MEDICINE_NAME));
		String medicineDose = cursor.getString(cursor
				.getColumnIndexOrThrow(UserCatalogDB.KEY_DOSE));
		String timeZoneString = cursor.getString(cursor
				.getColumnIndexOrThrow(UserCatalogDB.KEY_TIME_ZONE));
		String timeString = cursor.getString(cursor
				.getColumnIndexOrThrow(UserCatalogDB.KEY_TIME));
		String startDateString = cursor.getString(cursor
				.getColumnIndexOrThrow(UserCatalogDB.KEY_START_DATE));
		String endDateString = cursor.getString(cursor
				.getColumnIndexOrThrow(UserCatalogDB.KEY_END_DATE));
		repeatSchedule = cursor.getString(cursor
				.getColumnIndexOrThrow(UserCatalogDB.KEY_REPEAT_FREQUENCY));
		hourOfDay = Conveter.getHourOfDay(timeString, timeZoneString);
		minute = Conveter.getMinutes(timeString);
		startDate = Conveter.getCalendarDate(startDateString);
		endDate = Conveter.getCalendarDate(endDateString);

		if (medicineDose.isEmpty()) {
			contentText = userName + ", time to take " + medicineName;
		} else {
			contentText = userName + ", time to take " + medicineDose + " "
					+ medicineName;
		}

		return contentText;
	}

}
