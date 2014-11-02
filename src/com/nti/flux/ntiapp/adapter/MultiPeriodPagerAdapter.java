package com.nti.flux.ntiapp.adapter;

import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.nti.flux.ntiapp.view.PeriodView;

public class MultiPeriodPagerAdapter extends PagerAdapter{
	private List<PeriodView> periods;
	
	public MultiPeriodPagerAdapter(Context context, List<PeriodView> periods) {
        this.periods = periods;
    }
	
	@Override
    public Object instantiateItem(ViewGroup collection, int position){
		collection.addView(periods.get(position));
		return periods.get(position);
	}
	@Override
	public void destroyItem(View collection, int position, Object view){
		((ViewPager)collection).removeView((PeriodView)view);
	}
	
	@Override
	public int getCount(){
		return periods.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

}
