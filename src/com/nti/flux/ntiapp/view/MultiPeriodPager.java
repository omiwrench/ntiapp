package com.nti.flux.ntiapp.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class MultiPeriodPager extends ViewPager{
	private String TAG = MultiPeriodPager.class.getName();
	
	public MultiPeriodPager(Context context) {
		super(context);
	}
	public MultiPeriodPager(Context context, AttributeSet attrs){
		super(context, attrs);
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent event){
		Log.d(TAG, "touch event intercepted");
		if(event.getAction() == MotionEvent.ACTION_UP){
			Log.d(TAG, "action_up");
			return false;
		}
		else if(event.getAction() == MotionEvent.ACTION_DOWN){
			Log.d(TAG, "action_down");
			return true;
		}
		return true;
	}
}
