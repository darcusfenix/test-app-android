package com.example.testapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

	public final static String EXTRA_MESSAGE = "com.example.testapp.MESSAGE";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		boolean gpsEnabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		EditText editText = (EditText) findViewById(R.id.edit_message);
		if (!gpsEnabled) {
			editText.setText("no found GPS");
			// Create a dialog here that requests the user to enable GPS, and
			// use an intent
			// with the
			// android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS action
			// to take the user to the Settings screen to enable GPS when they
			// click "OK"
		}

	}

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) { // Inflate the
	 * menu; this adds items to the action bar if it is present.
	 * getMenuInflater().inflate(R.menu.main, menu); return true; }
	 * 
	 * @Override public boolean onOptionsItemSelected(MenuItem item) { // Handle
	 * action bar item clicks here. The action bar will // automatically handle
	 * clicks on the Home/Up button, so long // as you specify a parent activity
	 * in AndroidManifest.xml. int id = item.getItemId(); if (id ==
	 * R.id.action_settings) { return true; } return
	 * super.onOptionsItemSelected(item); }
	 */
	public void sendMessage(View view) {
		Intent inten = new Intent(this, DisplayMessageActivity.class);
		EditText editText = (EditText) findViewById(R.id.edit_message);
		String message = editText.getText().toString();
		inten.putExtra(EXTRA_MESSAGE, message);
		startActivity(inten);
	}
}
