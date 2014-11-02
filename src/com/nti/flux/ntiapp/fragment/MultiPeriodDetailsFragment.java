package com.nti.flux.ntiapp.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nti.flux.ntiapp.R;
import com.nti.flux.ntiapp.adapter.MultiPeriodDetailsPagerAdapter;
import com.nti.flux.ntiapp.model.Period;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.TitlePageIndicator;

public class MultiPeriodDetailsFragment extends Fragment{
	private static final String TAG = MultiPeriodDetailsFragment.class.getName();
	
	private List<PeriodDetailsFragment> periods = new ArrayList<PeriodDetailsFragment>();
	
	public static MultiPeriodDetailsFragment newInstance(List<Period> periods){
		Log.d(TAG, "Instancing MultiPeriodDetails");
		MultiPeriodDetailsFragment frag = new MultiPeriodDetailsFragment();
		for(Period period : periods){
			Log.d(TAG, "adding new details instance");
			frag.periods.add(PeriodDetailsFragment.newInstance(period));
		}
		return frag;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state){
		Log.d(TAG, "Creating multiDetails view");
		View v = inflater.inflate(R.layout.fragment_multi_period_details, container, false);
		ViewPager pager = (ViewPager)v.findViewById(R.id.pager_periods);
		PagerAdapter pagerAdapter = new MultiPeriodDetailsPagerAdapter(getActivity(), getChildFragmentManager(), periods);
		pager.setAdapter(pagerAdapter);
		CirclePageIndicator titleIndicator = (CirclePageIndicator)v.findViewById(R.id.pager_indicator);
		titleIndicator.setViewPager(pager);
		Log.d(TAG, "Returning pager");
		return v;
	}
}
