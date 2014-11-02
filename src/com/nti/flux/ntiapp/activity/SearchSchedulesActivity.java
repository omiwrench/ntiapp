package com.nti.flux.ntiapp.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;

import com.nti.flux.ntiapp.R;
import com.nti.flux.ntiapp.adapter.SearchPagerAdapter;

public class SearchSchedulesActivity extends FragmentActivity{
	private static final String TAG = SearchSchedulesActivity.class.getName();
	
	private ViewPager mPager;
	private SearchPagerAdapter mPagerAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_other_schedules);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		
		mPager = (ViewPager)findViewById(R.id.pager);
		mPagerAdapter = new SearchPagerAdapter(getSupportFragmentManager());
		mPager.setAdapter(mPagerAdapter);
		
	}
	
	public ViewPager getPager(){
		return mPager;
	}
	
	public void onClick(View v){
		mPagerAdapter.getScheduleFragment().onClick(v);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
	    if (keyCode == KeyEvent.KEYCODE_BACK && mPager.getCurrentItem() == 1) {
	        mPager.setCurrentItem(0, true);
	        return true;
	    } else {
	    	overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	    	return super.onKeyDown(keyCode, event);
	    }
	}
}
