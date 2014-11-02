package com.nti.flux.ntiapp.adapter;

import com.nti.flux.ntiapp.fragment.ScheduleFragment;
import com.nti.flux.ntiapp.fragment.SearchSchedulesFragment;
import com.nti.flux.ntiapp.model.Schedule;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

public class SearchPagerAdapter extends FragmentPagerAdapter{
	private static final String TAG = SearchPagerAdapter.class.getName();

	private Schedule schedule;
	private boolean errorOccurred = false;
	ScheduleFragment scheduleFragment;
	FragmentManager fm;
	
	public SearchPagerAdapter(FragmentManager fm) {
		super(fm);
		this.fm = fm;
		Log.d(TAG, "Instantiating adapter");
		schedule = new Schedule();
		scheduleFragment = ScheduleFragment.newInstance(schedule, errorOccurred);
	}

	@Override
	public Fragment getItem(int pos){
		Log.d(TAG, "getting item: " + pos);
		if(pos == 1){
			scheduleFragment = ScheduleFragment.newInstance(schedule, false);
			return scheduleFragment;
		}	
		else{
			return SearchSchedulesFragment.newInstance(this);
		}
	}
	
	public ScheduleFragment getScheduleFragment(){
		return scheduleFragment;
	}
	
	@Override
	public int getItemPosition(Object object){
		if(object instanceof ScheduleFragment)
			return POSITION_NONE;
		return POSITION_UNCHANGED;
	}

	@Override
	public int getCount(){
		return 2;
	}
	
	public void setSchedule(Schedule schedule){
		scheduleFragment.setSchedule(schedule);
		this.schedule = schedule;
		fm.beginTransaction().remove(scheduleFragment).commit();
		scheduleFragment = ScheduleFragment.newInstance(schedule, false);
		notifyDataSetChanged();
	}
	
	public void displayScheduleError(){
		errorOccurred = true;
	}
}
