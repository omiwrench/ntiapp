package com.nti.flux.ntiapp.adapter;

import java.util.List;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.nti.flux.ntiapp.fragment.PeriodDetailsFragment;

public class MultiPeriodDetailsPagerAdapter extends FragmentStatePagerAdapter{
	private List<PeriodDetailsFragment> periods;
	
	public MultiPeriodDetailsPagerAdapter(Context context, FragmentManager fm, List<PeriodDetailsFragment> periods) {
		super(fm);
		this.periods = periods;
	}
	
	@Override
	public Fragment getItem(int position){
		return periods.get(position);
	}
	
	@Override
	public int getCount(){
		return periods.size();
	}

}
