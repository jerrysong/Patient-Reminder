package com.example.patientreminder;

import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PendingAlarmReceiver extends BroadcastReceiver {
	public final static String PENDING_ALARM = "PENDING_ALARM";

	@Override
	public void onReceive(Context context, Intent intent) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		String uri = intent.getStringExtra("Uri");
		
		if(uri.isEmpty()){
			return;
		}

		Intent serviceIntent = new Intent(context, PendingAlarmService.class);
		serviceIntent.putExtra("Uri", uri);
		context.startService(serviceIntent);
	}
}
