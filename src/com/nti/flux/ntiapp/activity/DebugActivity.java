package com.nti.flux.ntiapp.activity;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;

import com.nti.flux.ntiapp.R;

public class DebugActivity extends Activity{
	private static final String TAG = DebugActivity.class.getName();
	
	@Override
	public void onCreate(Bundle state){
		super.onCreate(state);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_debug);
		
		TextView numUpdatesView = (TextView)findViewById(R.id.text_num_updates);
		int numUpdates = PreferenceManager.getDefaultSharedPreferences(this).getInt("num_database_updates", -1);
		Log.d(TAG, "numUpdates: " + numUpdates);
		numUpdatesView.setText("Antal uppdateringar av databasen: " + numUpdates);
	}
}
