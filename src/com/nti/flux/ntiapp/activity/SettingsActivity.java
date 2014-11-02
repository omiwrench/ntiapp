package com.nti.flux.ntiapp.activity;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

import com.nti.flux.ntiapp.R;
import com.nti.flux.ntiapp.dbhelper.UserScheduleDbHelper;
import com.nti.flux.ntiapp.helper.NotificationHelper;
import com.nti.flux.ntiapp.model.Schedule;

public class SettingsActivity extends PreferenceActivity{
	private static final String TAG = SettingsActivity.class.getName();
	
	public static final String KEY_SCHOOLSOFT_USERNAME = "pref_schoolsoft_username", KEY_SCHOOLSOFT_PASSWORD = "pref_schoolsoft_password";
	public static final String KEY_SHOW_QUICKSCHEDULE = "pref_show_quickschedule";
	
	SharedPreferences sharedPrefs;

	private OnSharedPreferenceChangeListener listener = new OnSharedPreferenceChangeListener() {
		
		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
				String key) {
			if(key.equals(KEY_SHOW_QUICKSCHEDULE)){
				if(!sharedPreferences.getBoolean(KEY_SHOW_QUICKSCHEDULE, false)){
					NotificationHelper.cancelScheduleNotification(SettingsActivity.this);
				}
				else{
					UserScheduleDbHelper dbHelper = new UserScheduleDbHelper(SettingsActivity.this);
					Schedule s = dbHelper.getSchedule();
					if(s == null){
						Intent updateDbIntent = new Intent("update_database");
						PendingIntent mStartIntent = PendingIntent.getBroadcast(SettingsActivity.this, 0, updateDbIntent, 0);
						try {
							mStartIntent.send();
						} catch (CanceledException e) {
							Log.e(TAG, "CanceledException thrown in fetchSchedule");
						}
					}
					NotificationHelper.updateScheduleNotification(SettingsActivity.this, s);
				}
			}
		}
	};
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle state){
		super.onCreate(state);
		addPreferencesFromResource(R.xml.preferences);
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		sharedPrefs.registerOnSharedPreferenceChangeListener(listener);
		
		Preference myPref = (Preference)findPreference("log_out");
		myPref.setOnPreferenceClickListener(new OnPreferenceClickListener(){
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				new AlertDialog.Builder(SettingsActivity.this)
				.setTitle("Bekräfta utloggning")
				.setMessage("Är du säker på att du vill logga ut?")
				.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
						prefs.edit().remove("pref_schoolsoft_username").remove("pref_schoolsoft_password").apply();
						finish();
					}
				})
				.setNegativeButton("Nej", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				})
				.show();
				return true;
			}
		});
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		sharedPrefs.unregisterOnSharedPreferenceChangeListener(listener);
	}
}
