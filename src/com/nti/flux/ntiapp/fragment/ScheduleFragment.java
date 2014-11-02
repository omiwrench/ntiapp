package com.nti.flux.ntiapp.fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nti.flux.ntiapp.R;
import com.nti.flux.ntiapp.adapter.DaySlidePagerAdapter;
import com.nti.flux.ntiapp.model.Period;
import com.nti.flux.ntiapp.model.Schedule;
import com.nti.flux.ntiapp.view.MultiPeriodView;

public class ScheduleFragment extends Fragment{
	private static final String TAG = ScheduleFragment.class.getName();
	
	private Schedule schedule;
	
	LinearLayout detailsContainer;
	View root;
	View darkenView;
	
	ViewPager daysPager;

	public static ScheduleFragment newInstance(Schedule schedule, Boolean error){
		ScheduleFragment sf = new ScheduleFragment();
		sf.setSchedule(schedule);
		return sf;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state){
		View v;
		if(schedule != null && schedule.getDays().size() > 0){
			v = inflater.inflate(R.layout.fragment_schedule, container, false);
			detailsContainer = (LinearLayout)v.findViewById(R.id.details_layout);
			darkenView = v.findViewById(R.id.darken_layout);
			darkenView.setVisibility(View.GONE);
			
			ViewGroup tabs = (ViewGroup)v.findViewById(R.id.tabs);
			final List<TextView> titles = new ArrayList<TextView>();
			for(int i=0;i<tabs.getChildCount();i++){
				TextView t = (TextView)tabs.getChildAt(i);
				Typeface titleFont = Typeface.createFromAsset(getActivity().getAssets(), getString(R.string.font_main_menu_button));
				t.setTypeface(titleFont);
				titles.add(t);
			}
			
			ViewPager pager;
			PagerAdapter pagerAdapter;
			pager = (ViewPager)v.findViewById(R.id.days_pager);
			pagerAdapter = new DaySlidePagerAdapter(getActivity(), getChildFragmentManager(), schedule.getDays());
			pager.setOnPageChangeListener(new OnPageChangeListener() {
				
				@Override
				public void onPageSelected(int pos) {
			    	TextView title = titles.get(pos);
			    	title.setAlpha(1.0f);
			    	for(int i=0;i<titles.size();i++){
			    		if(i!=pos){
			    			TextView mTitle = titles.get(i);
			    			mTitle.setAlpha(0.6f);
			    			}
			    		}
			    	}
				
				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {}
				
				@Override
				public void onPageScrollStateChanged(int arg0) {}
			});
			pager.setAdapter(pagerAdapter);
			
			Calendar calendar = Calendar.getInstance();
			int day = calendar.get(Calendar.DAY_OF_WEEK);
			switch(day){
			case Calendar.MONDAY:
				pager.setCurrentItem(0);
				break;
			case Calendar.TUESDAY:
				pager.setCurrentItem(1);
				break;
			case Calendar.WEDNESDAY:
				pager.setCurrentItem(2);
				break;
			case Calendar.THURSDAY:
				pager.setCurrentItem(3);
				break;
			case Calendar.FRIDAY:
				pager.setCurrentItem(4);
				break;
			}
			
			daysPager = pager;
			
			//This will dismiss a details view
			darkenView.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					if(detailsContainer.hasFocus()){
						root.requestFocus();
						fadeOutDetailsView();
					}
				}
			});
		}
		else{
			Log.e(TAG, "No schedule, cannot inflate schedule view");
			v = new View(getActivity());
			v.setBackgroundColor(getResources().getColor(R.color.nti_color));
		}
		root = v;
		return v;
	}
	
	public void onClick(View v){
		if(daysPager != null){
			switch(v.getId()){
			case R.id.day_0:{
				daysPager.setCurrentItem(0);
				break;
			}
			case R.id.day_1:{
				daysPager.setCurrentItem(1);
				break;
			}
			case R.id.day_2:{
				daysPager.setCurrentItem(2);
				break;
			}
			case R.id.day_3:{
				daysPager.setCurrentItem(3);
				break;
			}
			case R.id.day_4:{
				daysPager.setCurrentItem(4);
				break;
			}
			}
		}
	}
	
	public void listenOnClick(View v, final List<Period> p, final boolean multiPeriod){
		v.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				if(!detailsContainer.hasFocus()){
					detailsContainer.requestFocus();
					if(multiPeriod)
						getFragmentManager().beginTransaction().replace(R.id.details_layout, MultiPeriodDetailsFragment.newInstance(p), "detailsFragment").commit();
					else
						getFragmentManager().beginTransaction().replace(R.id.details_layout, PeriodDetailsFragment.newInstance(p.get(0)), "detailsFragment").commit();
					fadeInDetailsView();
				}
			}
		});
	}
	
	private void fadeInDetailsView(){
		Log.d(TAG, "Fading in details view");
		Animation fadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in_accelerate_quick);
		fadeIn.setFillAfter(true);
		detailsContainer.startAnimation(fadeIn);
		darkenView.setVisibility(View.VISIBLE);
		darkenView.startAnimation(fadeIn);
	}
	private void fadeOutDetailsView(){
		Animation fadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out_accelerate_quick);
		fadeOut.setFillAfter(true);
		fadeOut.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				darkenView.clearAnimation();
				darkenView.setVisibility(View.GONE);
				if(getFragmentManager().findFragmentByTag("detailsFragment") != null)
					getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag("detailsFragment")).commit();
			}
		});
		detailsContainer.startAnimation(fadeOut);
		darkenView.startAnimation(fadeOut);
	}
	
	public Schedule getSchedule() {
		return schedule;
	}
	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}
}
