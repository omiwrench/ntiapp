package com.nti.flux.ntiapp.helper;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import com.nti.flux.ntiapp.R;
import com.nti.flux.ntiapp.activity.UserScheduleActivity;
import com.nti.flux.ntiapp.model.Period;
import com.nti.flux.ntiapp.model.Schedule;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class NotificationHelper {
	private static final String TAG = NotificationHelper.class.getName();
	
	private static final long TIME_START_NOTIFICATION = 1000*60*60*5; //05.00
	private static final long TIME_END_NOTIFICATION = 1000*60*60*17; //17.00
	public static final int NOTIFICATION_ID = 13371337;

	public static void updateScheduleNotification(Context ctx, Schedule schedule){
		boolean showNotification = PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean("pref_show_quickschedule", true);
		NotificationManager manager = (NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		if(showNotification && schedule != null && schedule.getDays() != null){
			
			GregorianCalendar c = new GregorianCalendar();
			int day = c.get(Calendar.DAY_OF_WEEK);
			if(day >= 2 && day <= 6){
				Log.d(TAG, "-------------------> school day");
				
				String timeString = c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE);
				DateFormat formatter = new SimpleDateFormat("HH:mm", new Locale("sv", "SE"));
				Time currTime = new Time(0);
				try{
					currTime = new Time(formatter.parse(timeString).getTime());
					if(currTime.after(new Time(TIME_START_NOTIFICATION)) && currTime.before(new Time(TIME_END_NOTIFICATION))
							&& schedule.getDays().get(day-2).getNextPeriod(currTime) != null){
						Period nextPeriod = schedule.getDays().get(day-2).getNextPeriod(currTime);
						long timeDiff = nextPeriod.getStartTime().getTime() - currTime.getTime();

						String contentText = "Om ";		

						long hoursL = timeDiff / 1000 / 60 / 60;
						int hours = Math.round((float)hoursL);					
						int minutes = (int) (timeDiff - hours*60*60*1000) / 1000 / 60;

						if(hours > 1)
							contentText = contentText + hours + " timmar och ";
						else if(hours == 1){
							if(minutes != 0)
								contentText = contentText + hours + " timme och ";
							else
								contentText = contentText + hours + " timme";
						}
							
						if(minutes > 1)
							contentText = contentText + minutes + " minuter";
						else if(minutes == 1)
							contentText = contentText + minutes + " minut";
						
						Intent startSplashIntent = new Intent(ctx, UserScheduleActivity.class);
						startSplashIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
						PendingIntent intent = PendingIntent.getActivity(ctx, 0, startSplashIntent, 0);
						
						NotificationCompat.Builder mBuilder =
								new NotificationCompat.Builder(ctx)
						.setSmallIcon(R.drawable.logo_white)
						.setContentTitle(nextPeriod.getRoom())
						.setContentText(contentText)
						.setContentIntent(intent)
						.setOngoing(true);
						manager.notify(NOTIFICATION_ID, mBuilder.build());
						Log.d(TAG, "----------------> notification built.");
					}
					else{
						Log.i(TAG, "Current time was not within school time interval");
						try{
							manager.cancel(NOTIFICATION_ID);
						}
						catch(NullPointerException e){}
					}
				}
				catch(ParseException e){
					Log.e(TAG, "Could not parse current time, aborting");
				}
			}
			else{
				Log.e(TAG, "day was < 2 or > 6");
			}
		}
		else if(schedule == null){
			Log.e(TAG, "Schedule was null");
		}
		else{
			try{
				manager.cancel(NOTIFICATION_ID);
				Log.i(TAG, "Notification removed");
			}
			catch(NullPointerException e){}
		}
	}
	
	public static void cancelScheduleNotification(Context ctx){
		NotificationManager manager = (NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		try{
			manager.cancel(NOTIFICATION_ID);
		}
		catch(NullPointerException e){
			Log.e(TAG, "Could not cancel schedule notification");
		}
	}
}
