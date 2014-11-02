package com.nti.flux.ntiapp.helper;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ProgressBar;

import com.nti.flux.ntiapp.dbhelper.NewsDbHelper;
import com.nti.flux.ntiapp.dbhelper.RestaurantsDbHelper;
import com.nti.flux.ntiapp.dbhelper.ScheduleDbHelper;
import com.nti.flux.ntiapp.dbhelper.UserScheduleDbHelper;
import com.nti.flux.ntiapp.dbhelper.SearchDbHelper;
import com.nti.flux.ntiapp.exception.InvalidSchoolsoftAuthException;
import com.nti.flux.ntiapp.model.Person;
import com.nti.flux.ntiapp.model.Restaurants;
import com.nti.flux.ntiapp.model.Schedule;
import com.nti.flux.ntiapp.service.NtiService;

public class DatabaseUpdater {
	private static final String TAG = DatabaseUpdater.class.getName();

	private Context context;
	
	public DatabaseUpdater(Context context){
		this.context = context;
	}
	
	public void updateDatabase(){
		new UpdateTask().execute();
	}
	public void updateDatabaseWithProgress(Runnable onFinish, ProgressBar progBar){
		new UpdateTask(onFinish, progBar).execute();
	}
	
	private class UpdateTask extends AsyncTask<Void, Integer, Void>{

		private Runnable onFinish;
		private ProgressBar pb;
		
		public UpdateTask(){}
		
		public UpdateTask(Runnable onFinish, ProgressBar pb){
			this.onFinish = onFinish;
			this.pb = pb;
			pb.setMax(10000);
		}
		
		@Override
		protected Void doInBackground(Void... arg0) {
			NtiService.updatingUserSchedule = true;
			NtiService.updatingDatabase = true;
			ImportSchoolsoft iS;
			UserScheduleDbHelper userScheduleDb;
			SearchDbHelper searchDb;
			NewsDbHelper newsDb;
			ScheduleDbHelper scheduleDb;
			RestaurantsDbHelper restaurantsDb;
			int max = 0;
			if(pb != null){
				max = pb.getMax();
			}
			try{
				iS = new ImportSchoolsoft(context);
				
				userScheduleDb = new UserScheduleDbHelper(context);
				searchDb = new SearchDbHelper(context);
				newsDb = new NewsDbHelper(context);
				scheduleDb = new ScheduleDbHelper(context);
				restaurantsDb = new RestaurantsDbHelper(context);
				
				searchDb.clear();
				scheduleDb.clear();
				
				Schedule schedule = iS.getScheduleForAppUser();
				if(schedule.getDays() != null){
					userScheduleDb.addSchedule(schedule);	
				}
				NtiService.updatingUserSchedule = false;
				publishProgress(max/6);
				
				NtiService.updatingStudents = true;
				List<Person> students = iS.getAllStudents();
				searchDb.addStudents(students);
				publishProgress(max/3);
				
				List<Person> teachers = iS.getAllTeachers();
				searchDb.addTeachers(teachers);
				NtiService.updatingStudents = false;
				newsDb.setNews(iS.getNews());
				publishProgress(max/6);
				
				RestaurantsHelper helper = new RestaurantsHelper(context);
				Restaurants r = helper.getRestaurants();
				restaurantsDb.setRestaurants(r);
				
				//This is debug stuff, to see that it doesn't update too often
				int numUpdates = PreferenceManager.getDefaultSharedPreferences(context).getInt("num_database_updates", 0);
				PreferenceManager.getDefaultSharedPreferences(context).edit().putInt("num_database_updates", numUpdates+1).apply();
				PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("database_filled", true).apply();
				
			} catch (InvalidSchoolsoftAuthException e) {
				Log.e(TAG, e.getMessage());
			}
			NtiService.updatingStudents = false;
			NtiService.updatingDatabase = false;
			return null;
		}
		
		protected void onProgressUpdate(Integer... progress){
			if(pb != null){
				pb.setProgress(pb.getProgress() + progress[0]);
			}
		}
		protected void onPostExecute(Void unused){
			if(onFinish != null)
				onFinish.run();
		}
	}
	
	private class ScheduleLoaded implements Runnable{
		int i;
		ProgressBar pb;
		int numRuns = 0;
		public ScheduleLoaded(int i, ProgressBar pb){
			this.i = i;
			this.pb = pb;
		}
		@Override
		public void run() {
			if(numRuns % 10 == 0){
				int result = pb.getProgress() + (pb.getMax()/4)/(i/10);
				pb.setProgress(result);
				Log.d(DatabaseUpdater.TAG, "i: " + i);
				Log.d(DatabaseUpdater.TAG, "pb.getProgress: " + pb.getProgress());
				Log.d(DatabaseUpdater.TAG, "pb.getMax: " + pb.getMax());
				Log.d(DatabaseUpdater.TAG, "setting progress to: " + result);
			}
			numRuns++;
		}
	}
}
