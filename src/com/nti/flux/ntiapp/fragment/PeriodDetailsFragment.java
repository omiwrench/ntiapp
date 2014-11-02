package com.nti.flux.ntiapp.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nti.flux.ntiapp.R;
import com.nti.flux.ntiapp.model.Period;

public class PeriodDetailsFragment extends Fragment{
	private static final String TAG = PeriodDetailsFragment.class.getName();
	
	private Period period;

	public static PeriodDetailsFragment newInstance(Period period){
		PeriodDetailsFragment frag = new PeriodDetailsFragment();
		frag.setPeriod(period);
		return frag;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state){
		View v;
		if(period != null){
			v = inflater.inflate(R.layout.fragment_period_details, container, false);
			
			Typeface titleFont = Typeface.createFromAsset(getActivity().getAssets(), getString(R.string.font_main_menu_button));
			Typeface contentFont = Typeface.createFromAsset(getActivity().getAssets(), getResources().getString(R.string.font_content));
			
			TextView subject = (TextView) v.findViewById(R.id.subject);
			TextView time = (TextView) v.findViewById(R.id.time);
			TextView room = (TextView) v.findViewById(R.id.room);
			TextView teacher = (TextView)v.findViewById(R.id.teacher);
			
			subject.setTypeface(titleFont);
			time.setTypeface(contentFont);
			room.setTypeface(contentFont);
			teacher.setTypeface(contentFont);
			subject.setText(period.getSubject());
			time.setText(period.getStartTimeString() + " - " + period.getEndTimeString());
			room.setText(period.getRoom());
			if(period.getTeacher() != null){
				teacher.setText(period.getTeacher());
			}
			if(subject.getText().length() > 30){
				subject.setTextSize(TypedValue.COMPLEX_UNIT_PX, getActivity().getResources().getDimensionPixelSize(R.dimen.text_content_smaller));
			}
		}
		else{
			Log.e(TAG, "Cannot create PeriodDetailsFragment view without a period");
			v = new View(getActivity());
		}
		return v;
	}
	
	public Period getPeriod() {
		return period;
	}
	public void setPeriod(Period period) {
		this.period = period;
	}
}
