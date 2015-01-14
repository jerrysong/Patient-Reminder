package com.example.patientreminder.activity;

import java.util.GregorianCalendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.widget.TextView;

import com.example.patientreminder.CalendarView;
import com.example.patientreminder.Cell;
import com.example.patientreminder.Conveter;
import com.example.patientreminder.R;

public class CalendarActivity extends Activity implements
		CalendarView.OnCellTouchListener {
	
	public static final int BROWSE_HISTORY_CODE = 1;
	public static final String MIME_TYPE = "vnd.android.cursor.dir/vnd.example.patientreminder.activity.date";
	private CalendarView mView = null;
	private TextView mTitle = null;
	private Handler mHandler = new Handler();
	private String userName = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calendar);
		mView = (CalendarView) findViewById(R.id.calendar);
		mView.setOnCellTouchListener(this);
		mTitle = (TextView) findViewById(R.id.text_calendar_current_time);
		userName = getIntent().getStringExtra("UserName");
		refreshTitle();
	}

	public void onTouch(Cell cell) {
		final int day = cell.getDayOfMonth();
		if (mView.firstDay(day)) {
			mView.previousMonth();
			refreshTitle();
		} else if (mView.lastDay(day)) {
			mView.nextMonth();
			refreshTitle();
		}

		mHandler.post(new Runnable() {
			public void run() {
				GregorianCalendar gregorianCalendar = new GregorianCalendar(
						mView.getYear(), mView.getMonth(), day);
				String calendarString = Conveter.getStringDate(gregorianCalendar);
				
				Intent intent = new Intent(CalendarActivity.this,
						BrowseHistoryActivity.class);				
				intent.putExtra("Date", calendarString);
				intent.putExtra("UserName", userName);
				
				startActivityForResult(intent, BROWSE_HISTORY_CODE);
			}
		});
	}

	@SuppressWarnings("deprecation")
	private void refreshTitle() {
		mTitle.setText(DateUtils.getMonthString(mView.getMonth(),
				DateUtils.LENGTH_LONG) + " " + mView.getYear());
	}
}