package com.nti.flux.ntiapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.nti.flux.ntiapp.activity.CreditsActivity;
import com.nti.flux.ntiapp.activity.NewsActivity;
import com.nti.flux.ntiapp.activity.RestaurantsActivity;
import com.nti.flux.ntiapp.activity.SearchSchedulesActivity;
import com.nti.flux.ntiapp.activity.SettingsActivity;
import com.nti.flux.ntiapp.activity.UserScheduleActivity;
import com.nti.flux.ntiapp.fragment.DatabaseUpdatingFragment;
import com.nti.flux.ntiapp.fragment.FirstTimeFragment;
import com.nti.flux.ntiapp.fragment.SyncingFragment;
import com.nti.flux.ntiapp.helper.ViewHelper;
import com.nti.flux.ntiapp.service.NtiService;

public class SplashActivity extends FragmentActivity {
	private static final String TAG = SplashActivity.class.getName();
	
	ImageView splashImage;
	ViewGroup container;
	
	Animation fadeSplashInAnimation;
	Animation fadeInAnimation;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		
		fadeSplashInAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_animation);
		fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in_accelerate);
		AnimationListener fadeSplashInAnimListener = new AnimationListener(){
			@Override
			public void onAnimationEnd(Animation arg0) {
				Log.d(TAG, "Animation ended");
				fadeInMenu();
			}
			@Override
			public void onAnimationRepeat(Animation arg0) {}
			@Override
			public void onAnimationStart(Animation arg0) {}
		};
		fadeSplashInAnimation.setAnimationListener(fadeSplashInAnimListener);	
		
		container = (ViewGroup) findViewById(R.id.container_mainmenu);
		ViewGroup buttons = (ViewGroup) findViewById(R.id.mainMenu_buttons);
		buttons.setAlpha(0.0f);
		for(int i=0;i<buttons.getChildCount();i++){
			buttons.getChildAt(i).setOnClickListener(buttonsListener);
			Typeface tf = Typeface.createFromAsset(getAssets(), getString(R.string.font_main_menu_button));
			((Button)buttons.getChildAt(i)).setTypeface(tf);
		}
		
		splashImage = (ImageView)findViewById(R.id.splashImage);
		splashImage.startAnimation(fadeSplashInAnimation);
	}
	
	@Override
	public void onResume(){
		super.onResume();
		//Determine if the user is launching the app for the first time, if he is, display the login to schoolsoft fragment thing
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		if(NtiService.updatingDatabase){
			SyncingFragment frag = SyncingFragment.newInstance();
			frag.setOnFinish(new Runnable(){
				@Override
				public void run() {
					getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentByTag("updatingFragment")).commitAllowingStateLoss();
					findViewById(R.id.dimmer_view_splash).setAlpha(0.0f);
					ViewHelper.enableDisableViewGroup((ViewGroup)findViewById(R.id.container_mainmenu), true);
				}
			});
			getSupportFragmentManager().beginTransaction().replace(R.id.overlay_container_splash, frag, "updatingFragment").commitAllowingStateLoss();
			findViewById(R.id.dimmer_view_splash).setAlpha(0.5f);
		}
		else if(prefs.getString("pref_schoolsoft_username", "").isEmpty() || prefs.getString("pref_schoolsoft_password", "").isEmpty()){
			querySchoolsoftLoginExists();
		}
		else if(!(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("database_filled", false))){
			ViewHelper.enableDisableViewGroup((ViewGroup)findViewById(R.id.container_mainmenu), false);
			Runnable onFinish = new Runnable(){
				@Override
				public void run() {
					getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentByTag("updatingFragment")).commitAllowingStateLoss();
					findViewById(R.id.dimmer_view_splash).setAlpha(0.0f);
					ViewHelper.enableDisableViewGroup((ViewGroup)findViewById(R.id.container_mainmenu), true);
				}
			};
			DatabaseUpdatingFragment frag = DatabaseUpdatingFragment.newInstance(onFinish);
			
			getSupportFragmentManager().beginTransaction().replace(R.id.overlay_container_splash, frag, "updatingFragment").commitAllowingStateLoss();
			findViewById(R.id.dimmer_view_splash).setAlpha(0.5f);
		}
		else{
			//Start service
			Intent intent = new Intent(this, NtiService.class);
			startService(intent);
		}
		if(container.getAlpha() == 0){
			AnimationListener listener = new AnimationListener(){

				@Override
				public void onAnimationEnd(Animation arg0) {
					container.setAlpha(1.0f);
				}
				@Override
				public void onAnimationRepeat(Animation arg0) {}
				@Override
				public void onAnimationStart(Animation arg0) {}
			};
			fadeInAnimation.setAnimationListener(listener);
			container.setAlpha(1.0f);
			container.startAnimation(fadeInAnimation);
		}
		
		
	} 
	
	public void fadeInMenu(){
		ViewGroup buttons = (ViewGroup) findViewById(R.id.mainMenu_buttons);
		buttons.setAlpha(1.0f);
		int buttonCount = buttons.getChildCount();
		for(int i=0;i<buttonCount;i++){
			Animation anim = AnimationUtils.loadAnimation(this, R.anim.fade_in_accelerate);
			final View child = (View) buttons.getChildAt(i);
			anim.setStartOffset(i*100);
			child.startAnimation(anim);
		}
	}
	
	public void launchView(final Class<?> target){
		Intent intent = new Intent(SplashActivity.this, target);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.splash, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case R.id.action_settings:
			launchView(SettingsActivity.class);
			return true;
		case R.id.action_update:
			ViewHelper.enableDisableViewGroup((ViewGroup)findViewById(R.id.container_mainmenu), false);
			Runnable onFinish = new Runnable(){
				@Override
				public void run(){
					getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentByTag("updatingFragment")).commitAllowingStateLoss();
					findViewById(R.id.dimmer_view_splash).setAlpha(0.0f);
					ViewHelper.enableDisableViewGroup((ViewGroup)findViewById(R.id.container_mainmenu), true);
				}
			};
			DatabaseUpdatingFragment frag = DatabaseUpdatingFragment.newInstance(onFinish);
			
			getSupportFragmentManager().beginTransaction().replace(R.id.overlay_container_splash, frag, "updatingFragment").commitAllowingStateLoss();
			findViewById(R.id.dimmer_view_splash).setAlpha(0.5f);
			return true;
		case R.id.action_credits:
			launchView(CreditsActivity.class);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private void querySchoolsoftLoginExists(){
		ViewHelper.enableDisableViewGroup((ViewGroup)findViewById(R.id.container_mainmenu), false);
		
		getSupportFragmentManager().beginTransaction().replace(R.id.overlay_container_splash, FirstTimeFragment.newInstance(this), "queryFragment").commitAllowingStateLoss();
		findViewById(R.id.dimmer_view_splash).setAlpha(0.5f);
	}
	public void exitLoginQuery(){
		getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentByTag("queryFragment")).commitAllowingStateLoss();
		findViewById(R.id.dimmer_view_splash).setAlpha(0.0f);
		ViewHelper.enableDisableViewGroup((ViewGroup)findViewById(R.id.container_mainmenu), true);
		
		//Start service
		Intent intent = new Intent(this, NtiService.class);
		startService(intent);
	}
	
	private OnClickListener buttonsListener = new OnClickListener(){
		public void onClick(View v){
			switch(v.getId()){
			case R.id.button_schedule:
				launchView(UserScheduleActivity.class);
				break;
			case R.id.button_other_schedules:
				launchView(SearchSchedulesActivity.class);
				break;
			case R.id.button_restaurants:
				launchView(RestaurantsActivity.class);
				break;
			/*case R.id.button_info:
				launchView(InfoActivity.class);
				break; */
			case R.id.button_news:
				launchView(NewsActivity.class);
				break;
			}
		}
	};
}
