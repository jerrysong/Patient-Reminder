package com.example.patientreminder.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.example.patientreminder.R;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    
    @Override
    protected void onDestroy(){
    	super.onDestroy();
    	SharedPreferences sharedPref = getSharedPreferences("AddPrescriptionActivity",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.clear();
		editor.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void getStarted(View view){
    	Intent intent = new Intent(this, ShowPatientsPanelActivity.class);
    	startActivity(intent);
    }  
    
    public void getAboutUs(View view){
    	Intent intent = new Intent(this, ShowAboutUsActivity.class);
    	startActivity(intent);
    }

}
