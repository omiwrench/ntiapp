package com.nti.flux.ntiapp.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.nti.flux.ntiapp.R;
import com.nti.flux.ntiapp.dbhelper.UserScheduleDbHelper;
import com.nti.flux.ntiapp.fragment.ScheduleFragment;
import com.nti.flux.ntiapp.model.Schedule;
import com.nti.flux.ntiapp.service.NtiService;

public class UserScheduleActivity extends FragmentActivity{
	private static final String TAG  = UserScheduleActivity.class.getName();
	
	private ScheduleFragment frag;
	
	@Override
	public void onCreate(Bundle state){
		super.onCreate(state);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_schedule);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		
		if(NtiService.updatingUserSchedule)
			new WaitForDb(this).execute();
		else
			getSchedule();
	}
	private void getSchedule(){
		new LoadScheduleTask(this).execute();
	}
	
	public void onClick(View v){
		if(frag != null){
			frag.onClick(v);
		}
	}
	
	private void showSchedule(Fragment frag){
		Log.d(TAG, "Showing schedule");
		View container = findViewById(R.id.container_schedule);
		Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in_accelerate_quick);
		fadeIn.setFillAfter(true);
		getSupportFragmentManager().beginTransaction().add(R.id.container_schedule, frag).commit();
		container.startAnimation(fadeIn);
	}
	
	private class LoadScheduleTask extends AsyncTask<Void, Void, Boolean>{
		Context context;
		ProgressDialog pd;
		Schedule schedule;
		
		LoadScheduleTask(Context contex){
			this.context = contex;
			pd = new ProgressDialog(context);
		}
		protected void onPreExecute(){
			/*pd.setTitle("Hämtar schema...");
			pd.setMessage("Vänta...");
			pd.setCancelable(false);
			pd.show();*/
		}
		protected void onPostExecute(Boolean result){
			Log.d(TAG, "PostExecute");
			if(pd.isShowing())
				pd.dismiss();
			frag = ScheduleFragment.newInstance(schedule, false);
			showSchedule(frag);
		}
		
		@Override
		protected Boolean doInBackground(Void... arg0) {
			UserScheduleDbHelper helper = new UserScheduleDbHelper(context);
			schedule = helper.getSchedule();
			return true;
		}
		
	}
	
	@Override
	public void onBackPressed() {
	    super.onBackPressed();
	    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}
	@Override
	protected void onResume(){
		super.onResume();
	}
	
	@Override
	protected void onPause(){
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	private class WaitForDb extends AsyncTask<Void, Void, Boolean>{
		
		private ProgressDialog pd;
		
		public WaitForDb(Context context){
			pd = new ProgressDialog(context);
		}
		
		protected void onPreExecute(){
			pd.setTitle("Ett ögonblick...");
			pd.setMessage("Hämtar ditt schema...");
			pd.setCancelable(false);
			pd.show();
		}
		protected void onPostExecute(Boolean result){
			getSchedule();
			pd.dismiss();
		}
		@Override
		protected Boolean doInBackground(Void... arg0) {
			while(NtiService.updatingUserSchedule){}
			return true;
		}
		
	}
}
