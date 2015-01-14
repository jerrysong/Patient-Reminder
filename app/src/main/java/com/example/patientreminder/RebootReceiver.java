package com.example.patientreminder;

import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

public class RebootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		//Toast.makeText(context, "Receive Success", Toast.LENGTH_LONG).show();
		launchAllAlarmService(context);
	}

	private void launchAllAlarmService(Context context) {
		String[] projection = { UserCatalogDB._ID, UserCatalogDB.KEY_TIME,
				UserCatalogDB.KEY_TIME_ZONE,
				UserCatalogDB.KEY_REPEAT_FREQUENCY,
				UserCatalogDB.KEY_START_DATE, UserCatalogDB.KEY_END_DATE };
		String select = "(" + UserCatalogDB.KEY_USER_REGISTER_ROW + "='false')";
		Cursor cursor = context.getContentResolver().query(
				MyContentProvider.CONTENT_URI_USER_CATALOG, projection, select,
				null, null);		
		if(cursor == null){
			return;
		}

		boolean notLastEntry = true;
		if(!cursor.moveToFirst()){			
			return;
		}
		
		while (notLastEntry) {							
			int id = cursor.getInt(cursor
					.getColumnIndexOrThrow(UserCatalogDB._ID));
			String timeString = cursor.getString(cursor
					.getColumnIndexOrThrow(UserCatalogDB.KEY_TIME));
			String timeZoneString = cursor.getString(cursor
					.getColumnIndexOrThrow(UserCatalogDB.KEY_TIME_ZONE));
			String repeatSchedule = cursor.getString(cursor
					.getColumnIndexOrThrow(UserCatalogDB.KEY_REPEAT_FREQUENCY));
			String startDateString = cursor.getString(cursor
					.getColumnIndexOrThrow(UserCatalogDB.KEY_START_DATE));
			String endDateString = cursor.getString(cursor
					.getColumnIndexOrThrow(UserCatalogDB.KEY_END_DATE));

			Uri uri = Uri.parse(MyContentProvider.CONTENT_URI_USER_CATALOG
					+ "/" + id);			
			int hourOfDay = Conveter.getHourOfDay(timeString, timeZoneString);
			int minute = Conveter.getMinutes(timeString);
			GregorianCalendar startDate = null;
			GregorianCalendar endDate = null;
			try {
				startDate = Conveter.getCalendarDate(startDateString);
				endDate = Conveter.getCalendarDate(endDateString);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
			calendar.set(Calendar.MINUTE, minute);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);

			Calendar scheduledCalendar = AlarmScheduler.getScheduledCalendar(
					calendar, repeatSchedule, context);

			boolean validation = AlarmScheduler.validateCalendar(
					scheduledCalendar, repeatSchedule, startDate, endDate,
					context);
			if (!validation) {
				notLastEntry = cursor.moveToNext();
				continue;
			}

			Intent intent = new Intent(AlarmReceiver.ACTION_ALARM, uri);
			intent.setClass(context, AlarmReceiver.class);
			intent.putExtra("Uri", uri.toString());

			PendingIntent pending = PendingIntent.getBroadcast(context, 0,
					intent, 0);
			AlarmManager alarm = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);

			alarm.set(AlarmManager.RTC_WAKEUP,
					scheduledCalendar.getTimeInMillis(), pending);

			notLastEntry = cursor.moveToNext();
		}
		
		return;
	}
}
