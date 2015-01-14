package com.example.patientreminder.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.example.patientreminder.R;
import com.example.patientreminder.TabManager;

public class AddPrescriptionActivity extends FragmentActivity{
	private TabHost mTabHost;
	private TabManager mTabManager;	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		String userName = intent.getStringExtra("UserName");
		setTitle(userName);
		
		SharedPreferences sharedPref = getSharedPreferences("AddPrescriptionActivity",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString("userName", userName);
		editor.apply();

		setContentView(R.layout.activity_add_prescription);	
		setFragmentTabs();
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
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void setFragmentTabs(){
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();

		mTabManager = new TabManager(this, mTabHost, R.id.realtabcontent);

		mTabHost.setCurrentTab(0);
		mTabManager.addTab(mTabHost.newTabSpec("SetPrescriptionProfile")
				.setIndicator("Medication"), SetPrescriptionProfileFragment.class,
				null);

		mTabHost.setCurrentTab(1);
		mTabManager
				.addTab(mTabHost.newTabSpec("SetAlarmSchedule").setIndicator(
						"Schedule Med Reminder"), SetAlarmScheduleFragment.class, null);

		for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {
			TextView labelTextView = (TextView) mTabHost.getTabWidget()
					.getChildAt(i).findViewById(android.R.id.title);
			labelTextView.setTextColor(getResources().getColor(R.color.black));
		}

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenWidth = dm.widthPixels;

		TabWidget tabWidget = mTabHost.getTabWidget();
		int count = tabWidget.getChildCount();
		for (int i = 0; i < count; i++) {
			tabWidget.getChildTabViewAt(i).setMinimumWidth(
					(screenWidth) / count);
		}
	}
}