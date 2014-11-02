package com.nti.flux.ntiapp.service;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.preference.PreferenceManager;
import android.util.Log;

import com.nti.flux.ntiapp.activity.SettingsActivity;
import com.nti.flux.ntiapp.dbhelper.UserScheduleDbHelper;
import com.nti.flux.ntiapp.helper.NotificationHelper;
import com.nti.flux.ntiapp.model.Schedule;

public class NtiService extends Service{
	private static final String TAG = NtiService.class.getName();

	private Looper mServiceLooper;
	private ServiceHandler mServiceHandler;
	private static BroadcastReceiver tickReceiver;
	private Schedule schedule;
	
	public static boolean updatingUserSchedule = false;
	public static boolean updatingStudents = false;
	public static boolean updatingDatabase = false;

	private final class ServiceHandler extends Handler{
		public ServiceHandler(Looper looper){
			super(looper);
		}
		@Override
		public void handleMessage(Message msg){
			//do something
		}
	}
	
	@Override
	public void onCreate(){
		Log.d(TAG, "Creating NtiService");
		HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();
		
		mServiceLooper = thread.getLooper();
		mServiceHandler = new ServiceHandler(mServiceLooper);
		
		tickReceiver = new BroadcastReceiver(){
			@Override
			public void onReceive(Context context, Intent intent){
				if(intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0){
					updateNotification();
				}
				else{
					Log.d(TAG, "Intent wasn't ACTION_TIME_TICK");
				}
			}
		};
		
		registerReceiver(tickReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
		
		Intent updateDbIntent = new Intent("update_database");
		PendingIntent mStartIntent = PendingIntent.getBroadcast(this, 0, updateDbIntent, 0);
		AlarmManager mAlarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		
		mAlarm.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, mStartIntent);
		
		if(schedule == null){
			Log.d(TAG, "Schedule was null, fetching new");
			schedule = fetchSchedule();
		}
		if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(SettingsActivity.KEY_SHOW_QUICKSCHEDULE, false))
			updateNotification();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		// For each start request, send a message to start a job and deliver the
		// start ID so we know which request we're stopping when we finish the job
		Message msg = mServiceHandler.obtainMessage();
		msg.arg1 = startId;
		mServiceHandler.sendMessage(msg);

		// If we get killed, after returning from here, restart
		return START_STICKY;
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		Log.d(TAG, "onDestroy called for service");
		if(tickReceiver != null)
			Log.d(TAG, "Unregistering receiver");
			this.unregisterReceiver(tickReceiver);
		try{
			NotificationHelper.cancelScheduleNotification(this);
		}
		catch(NullPointerException e){
			Log.e(TAG, "Could not cancel notification");
		}
	}
	
	@Override
	public IBinder onBind(Intent intent){
		Log.e(TAG, "NtiService does not support binding");
		return null;
	}
	
	private void updateNotification(){
		if(schedule == null){
			if(fetchSchedule() != null)
				NotificationHelper.updateScheduleNotification(this, fetchSchedule());
		}
		else{
			NotificationHelper.updateScheduleNotification(this, schedule);
		}
	} //END updateNotification
	
	private Schedule fetchSchedule(){
		UserScheduleDbHelper dbHelper = new UserScheduleDbHelper(this);
		Schedule s = dbHelper.getSchedule();
		if(s == null){
			Log.d(TAG, "dbHelper had no schedule, updating database");
			Intent updateDbIntent = new Intent("update_database");
			PendingIntent mStartIntent = PendingIntent.getBroadcast(this, 0, updateDbIntent, 0);
			try {
				mStartIntent.send();
			} catch (CanceledException e) {
				Log.e(TAG, "CanceledException thrown in fetchSchedule");
			}
		}
		return s;
	}
	
	public Schedule getSchedule(){
		if(schedule != null && schedule.getDays() != null){
			return schedule;
		}
		else{
			Log.d(TAG, "schedule was null, fetching schedule");
			return null;
		}
	}
	
	public void setSchedule(Schedule schedule){
		this.schedule = schedule;
	}
	
	public class MyBinder extends Binder {
		NtiService getService() {
			return NtiService.this;
		}
	}
}
