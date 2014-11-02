package com.nti.flux.ntiapp.model;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class Day {
	private static final String TAG = Day.class.getName();
	
	private List<Period> periods;
	
	public Day(){
		periods = new ArrayList<Period>();
	}
	
	public List<Period> getPeriods() {
		return periods;
	}
	public void setPeriods(List<Period> periods) {
		this.periods = periods;
	}
	
	public void addPeriod(Period period){
		periods.add(period);
	}
	
	public void addPeriodAt(int index, Period period){
		periods.add(index, period);
	}
	
	public void removePeriodAt(int index){
		periods.remove(index);
	}
	
	public Period getNextPeriod(Time time){
		for(Period period : periods){
			//Returns the first period that's after supplied time, so as long as they're sorted chronologically we should be fine...
			if(period.getStartTime().getTime() > time.getTime()){
				return period;
			}
		}
		//Log.d(TAG, "Next period could not be found, assuming end of day");
		return null;
	}
}
