package com.example.patientreminder;

import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {
	public final static String ACTION_ALARM = "ACTION_ALARM";
	
	@Override
	public void onReceive(Context context, Intent intent) {				
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		String uri = intent.getStringExtra("Uri");
		
		if(uri.isEmpty()){
			return;
		}
		
		Intent serviceIntent = new Intent(context, AlarmService.class);
		serviceIntent.putExtra("Uri", uri);
		context.startService(serviceIntent);
	}
}
