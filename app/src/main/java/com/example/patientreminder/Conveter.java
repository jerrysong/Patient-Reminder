package com.example.patientreminder;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

@SuppressLint("SimpleDateFormat")
public class Conveter {

	public static String convertBitmapToString(Bitmap image) {
		if (image == null) {
			return null;
		}
		Bitmap immagex = image;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		byte[] b = baos.toByteArray();
		String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
		return imageEncoded;
	}

	public static Bitmap convertStringToBitmap(String input) {
		if (input == null) {
			return null;
		}
		byte[] decodedByte = Base64.decode(input, 0);
		return BitmapFactory
				.decodeByteArray(decodedByte, 0, decodedByte.length);
	}

	public static String getSavedTimeValue(ReminderModel reminderRow) {
		StringBuilder time = new StringBuilder();
		if (reminderRow.hourOfDay < 12) {
			time.append(String.valueOf(reminderRow.hourOfDay));
		} else {
			time.append(String.valueOf(reminderRow.hourOfDay - 12));
		}

		if (reminderRow.minute < 10) {
			time.append(":0" + String.valueOf(reminderRow.minute));
		} else {
			time.append(":" + String.valueOf(reminderRow.minute));
		}

		return time.toString();
	}

	public static String getSavedTimeZone(ReminderModel reminderRow) {
		String timeZone = null;
		if (reminderRow.hourOfDay < 12) {
			timeZone = "AM ";
		} else {
			timeZone = "PM ";
		}

		return timeZone;
	}

	public static int getHourOfDay(String time, String timeZone) {
		String[] timeValue = time.split(":");
		int hours = Integer.valueOf(timeValue[0]);
		if (timeZone.trim().equals("AM")) {
			return hours;
		} else if (timeZone.trim().equals("PM")) {
			return (hours + 12);
		}
		return 0;
	}

	public static int getMinutes(String time) {
		String[] timeValue = time.split(":");
		int minutes = Integer.valueOf(timeValue[1]);
		return minutes;
	}

	@SuppressWarnings("deprecation")
	public static GregorianCalendar getCalendarDate(String calendarString)
			throws ParseException {
		SimpleDateFormat fmt = new SimpleDateFormat("dd-MMM-yyyy");
		Date date = fmt.parse(calendarString);
		GregorianCalendar gregorianCalendar = new GregorianCalendar(
				date.getYear() + 1900, date.getMonth(), date.getDate());
		return gregorianCalendar;
	}

	@SuppressLint("SimpleDateFormat")
	public static String getStringDate(GregorianCalendar calendar) {
		SimpleDateFormat fmt = new SimpleDateFormat("dd-MMM-yyyy");
		fmt.setCalendar(calendar);
		String dateFormatted = fmt.format(calendar.getTime());
		return dateFormatted;
	}
}
