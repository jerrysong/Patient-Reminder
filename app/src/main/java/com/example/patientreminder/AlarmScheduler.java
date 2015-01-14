package com.example.patientreminder;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

public class AlarmScheduler {

	public static Calendar getScheduledCalendar(Calendar calendar,
			String repeatSchedule, Context context) {
		int dayDifference = 0;
		String[] repeatScheduleArray = context.getResources().getStringArray(
				R.array.repeat_schedule_array);

		if (repeatSchedule.equals(repeatScheduleArray[0])) {
			dayDifference = 8;
		} else if (repeatSchedule.equals(repeatScheduleArray[1])) {
			dayDifference = Calendar.MONDAY
					- calendar.get(Calendar.DAY_OF_WEEK);
		} else if (repeatSchedule.equals(repeatScheduleArray[2])) {
			dayDifference = Calendar.TUESDAY
					- calendar.get(Calendar.DAY_OF_WEEK);
		} else if (repeatSchedule.equals(repeatScheduleArray[3])) {
			dayDifference = Calendar.WEDNESDAY
					- calendar.get(Calendar.DAY_OF_WEEK);
		} else if (repeatSchedule.equals(repeatScheduleArray[4])) {
			dayDifference = Calendar.THURSDAY
					- calendar.get(Calendar.DAY_OF_WEEK);
		} else if (repeatSchedule.equals(repeatScheduleArray[5])) {
			dayDifference = Calendar.FRIDAY
					- calendar.get(Calendar.DAY_OF_WEEK);
		} else if (repeatSchedule.equals(repeatScheduleArray[6])) {
			dayDifference = Calendar.SATURDAY
					- calendar.get(Calendar.DAY_OF_WEEK);
		} else if (repeatSchedule.equals(repeatScheduleArray[7])) {
			dayDifference = Calendar.SUNDAY
					- calendar.get(Calendar.DAY_OF_WEEK);
		}

		if (dayDifference > 7) {
			if (calendar.getTimeInMillis() < Calendar.getInstance()
					.getTimeInMillis()) {
				calendar.add(Calendar.DATE, 1);
			}
		} else if (dayDifference == 0) {
			if (calendar.getTimeInMillis() < Calendar.getInstance()
					.getTimeInMillis()) {
				calendar.add(Calendar.DATE, 7);
			}
		} else if (dayDifference < 0) {
			dayDifference += 7;
			calendar.add(Calendar.DATE, dayDifference);
		} else {
			calendar.add(Calendar.DATE, dayDifference);
		}

		return calendar;
	}

	public static void saveUri(Calendar scheduledCalendar, Uri uri,
			String storedPath, Context context) {
		SharedPreferences sharedPref = context.getSharedPreferences(storedPath,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		String key = String.valueOf(scheduledCalendar.getTimeInMillis());
		editor.putString(key, uri.toString());
		editor.apply();
	}

	public static boolean validateCalendar(Calendar calendar,
			String repeatSchedule, GregorianCalendar startDate,
			GregorianCalendar endDate, Context context) {

		String[] repeatScheduleArray = context.getResources().getStringArray(
				R.array.repeat_schedule_array);
		while (calendar.getTimeInMillis() < startDate.getTimeInMillis()) {
			if (repeatSchedule.equals(repeatScheduleArray[0])) {
				calendar.add(Calendar.DATE, 1);
			} else {
				calendar.add(Calendar.DATE, 7);
			}
		}
		if (calendar.getTimeInMillis() > endDate.getTimeInMillis()) {
			return false;
		}
		return true;
	}
}
