package com.nti.flux.ntiapp.fragment;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.nti.flux.ntiapp.R;
import com.nti.flux.ntiapp.model.Day;
import com.nti.flux.ntiapp.model.Period;
import com.nti.flux.ntiapp.view.MultiPeriodView;
import com.nti.flux.ntiapp.view.PeriodView;

public class ScheduleDayFragment extends Fragment{
	private static final String TAG = ScheduleDayFragment.class.getName();
	private static final long INIT_OFFSET = 30000000l - 3600000l;
	private static Time avgDayStart = new Time(8, 20, 0);
	
	private Day day;
	private int dayNum = -1;
	
	public static ScheduleDayFragment newInstance(Day day, int dayNum){
		ScheduleDayFragment s = new ScheduleDayFragment();
		s.day = day;
		s.dayNum = dayNum;
		return s;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state){
		if(day != null && dayNum <= 5 && dayNum >= 0){
			View v = inflater.inflate(R.layout.fragment_day, container, false);
			LinearLayout periodsView = (LinearLayout)v.findViewById(R.id.periods_view);
			ScheduleFragment parent = (ScheduleFragment)getParentFragment();
			
			List<Period> periods = day.getPeriods();
			for(int j=0;j<periods.size();j++){
				Period period = periods.get(j);
				List<Period> mPeriods = new ArrayList<Period>();
				mPeriods.add(period);

				PeriodView mView = new PeriodView(getActivity());
				
				//If several periods overlap, create a multiperiodfragment
				if(j+1 != periods.size() && periods.get(j+1) != null &&
						periods.get(j+1).getStartTime().before(period.getEndTime())){
					
					Log.d(TAG, "period was multiperiod");
					
					int controlVar = 1;
					while(j+1 != periods.size() && periods.get(j+1).getStartTime().before(period.getEndTime())){
						
						period = periods.get(j + controlVar);
						mPeriods.add(period);
						
						j++;
					}
					long breakMillis = 0l;
					if(j != periods.size()-1)
						breakMillis = periods.get(j+1).getStartTime().getTime() - period.getEndTime().getTime();
					mView = new MultiPeriodView(getActivity(), mPeriods, breakMillis);
					
					View listenView = mView.findViewById(R.id.multiperiod_click_view);
					parent.listenOnClick(listenView, mPeriods, true);
				}
				//If they don't overlap, simply create a new PeriodView
				else{
					long breakMillis = 0l;
					if(j == 0 && period.getStartTime().getTime() > INIT_OFFSET){
						int startOffset = (int)(period.getStartTime().getTime() - avgDayStart.getTime())/50000;
						float paddingPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, startOffset, getActivity().getResources().getDisplayMetrics());
						periodsView.setPadding(0, (int)paddingPx, 0, 0);
					}
					if(j != periods.size()-1)
						breakMillis = periods.get(j+1).getStartTime().getTime() - period.getEndTime().getTime();
					mView = new PeriodView(getActivity(), period, breakMillis);
					parent.listenOnClick(mView, mPeriods, false);
				}
				periodsView.addView(mView);
			}	
			return v;
		}
		else{
			return null;
		}
	}
}
