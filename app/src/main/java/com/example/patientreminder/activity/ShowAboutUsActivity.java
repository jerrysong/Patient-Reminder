package com.example.patientreminder.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.Menu;
import android.widget.TextView;

import com.example.patientreminder.R;

public class ShowAboutUsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_about_us);
		
		TextView url = (TextView) findViewById(R.id.text_web_url);
		Linkify.addLinks(url, Linkify.ALL);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_about_us, menu);
		return true;
	}

}
