package com.nti.flux.ntiapp.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nti.flux.ntiapp.R;
import com.nti.flux.ntiapp.adapter.MultiPeriodPagerAdapter;
import com.nti.flux.ntiapp.model.Period;
import com.nti.flux.ntiapp.view.PeriodView;
import com.viewpagerindicator.CirclePageIndicator;

public class MultiPeriodFragment extends Fragment{
	private static final String TAG = MultiPeriodDetailsFragment.class.getName();
	
	private List<PeriodView> periods = new ArrayList<PeriodView>();
	
	public static MultiPeriodFragment newInstance(Context c, List<Period> periods){
		Log.d(TAG, "Instancing MultiPeriodDetails");
		MultiPeriodFragment frag = new MultiPeriodFragment();
		for(int i=0;i<periods.size();i++){
			Log.d(TAG, "adding new details instance");
			long breakMillis = 0;
			if(i != periods.size()-1)
				breakMillis = periods.get(i+1).getStartTime().getTime() - periods.get(i).getEndTime().getTime();
			frag.periods.add(new PeriodView(c, periods.get(i), breakMillis));
		}
		return frag;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state){
		Log.d(TAG, "Creating multiDetails view");
		View v = inflater.inflate(R.layout.fragment_multi_period_details, container, false);
		ViewPager pager = (ViewPager)v.findViewById(R.id.pager_periods);
		PagerAdapter pagerAdapter = new MultiPeriodPagerAdapter(getActivity(), periods);
		pager.setAdapter(pagerAdapter);
		CirclePageIndicator titleIndicator = (CirclePageIndicator)v.findViewById(R.id.pager_indicator);
		titleIndicator.setViewPager(pager);
		Log.d(TAG, "Returning pager");
		return v;
	}
}
