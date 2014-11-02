package com.nti.flux.ntiapp.view;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import com.nti.flux.ntiapp.R;
import com.nti.flux.ntiapp.adapter.MultiPeriodPagerAdapter;
import com.nti.flux.ntiapp.model.Period;
import com.viewpagerindicator.CirclePageIndicator;

public class MultiPeriodView extends PeriodView{
	private static final String TAG = MultiPeriodView.class.getName();

	private List<Time> startTimes = new ArrayList<Time>();
	private List<Time> endTimes = new ArrayList<Time>();
	private List<PeriodView> periods = new ArrayList<PeriodView>();
	private long breakMillis;
	
	private ViewPager pager;

	public MultiPeriodView(Context context) {
		super(context);
		init();
	}
	public MultiPeriodView(Context context, List<Period> periods, long breakMillis){
		super(context);
		for(int i=0;i<periods.size();i++){
			this.periods.add(new PeriodView(getContext(), periods.get(i), breakMillis));
			startTimes.add(periods.get(i).getStartTime());
			endTimes.add(periods.get(i).getEndTime());
		}
		this.breakMillis = breakMillis;
		init();
	}
	
	private void init(){
		LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.multi_period, this, true);
		
		//final so I can reference it from runnable
		pager = (ViewPager)findViewById(R.id.pager_periods);
		PagerAdapter pagerAdapter = new MultiPeriodPagerAdapter(getContext(), periods);
		pager.setAdapter(pagerAdapter);
		CirclePageIndicator titleIndicator = (CirclePageIndicator)findViewById(R.id.pager_indicator);
		titleIndicator.setViewPager(pager);
		
		//Set bottom margin proportionally to the next break's duration
		long value = breakMillis/50000;
		float marginPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getContext().getResources().getDisplayMetrics());
		
		long heightRaw = (getLatestTime().getTime() - getEarliestTime().getTime());
		long height = heightRaw/50000;
		float heightPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height, getContext().getResources().getDisplayMetrics());
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, (int)heightPx);
		params.setMargins(0, 0, 0, (int)marginPx);
		setLayoutParams(params);
		
		numPeriods = pager.getAdapter().getCount();
		
		handler.postDelayed(runnable, 3000);
	}
	
	private Time getEarliestTime(){
		Time startTime = new Time(86400000-1);
		for(Time time : startTimes){
			if(time.before(startTime))
				startTime = time;
		}
		return startTime;
	}
	private Time getLatestTime(){
		Time endTime = new Time(0);
		for(Time time : endTimes){
			if(time.after(endTime))
				endTime = time;
		}
		return endTime;
	}
	
	//This makes the viewpager switch views automatically
	Handler handler = new Handler();
	int pos = 0;
	//Gets instansiated in init()
	int numPeriods;
	Runnable runnable = new Runnable(){
		@Override
		public void run() {
			int p = pager.getCurrentItem();
			if(p >= numPeriods-1){
				pos = 0;
			}
			else{
				pos++;
			}
			pager.setCurrentItem(pos, true);
			handler.postDelayed(runnable, 3000);
		}
	};
	public List<Time> getStartTimes() {
		return startTimes;
	}
	public void setStartTimes(List<Time> startTimes) {
		this.startTimes = startTimes;
	}
	public List<Time> getEndTimes() {
		return endTimes;
	}
	public void setEndTimes(List<Time> endTimes) {
		this.endTimes = endTimes;
	}
}
