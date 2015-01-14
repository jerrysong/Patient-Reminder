package com.example.patientreminder;

import java.util.GregorianCalendar;

public class ReminderModel {
	public int hourOfDay;
	public int minute;
	public GregorianCalendar startDate;
	public GregorianCalendar endDate;
	public String medicineName;
	public String medicineDose;
	public String imageBitmapString;

	public ReminderModel(int hourOfDay, int minute,
			GregorianCalendar startDate, GregorianCalendar endDate,
			String medicineName, String medicineDose, String imageBitmapString) {
		this.hourOfDay = hourOfDay;
		this.minute = minute;
		this.startDate = startDate;
		this.endDate = endDate;
		this.medicineName = medicineName;
		this.medicineDose = medicineDose;
		this.imageBitmapString = imageBitmapString;
	}

	public boolean isEqual(ReminderModel model) {
		if (this.hourOfDay == model.hourOfDay && this.minute == model.minute
				&& this.startDate == model.startDate
				&& this.endDate == model.endDate
				&& this.medicineName.equals(model.medicineName)
				&& this.medicineDose.equals(model.medicineDose)
				&& this.imageBitmapString.equals(model.imageBitmapString)) {
			return true;
		}
		return false;
	}
}
