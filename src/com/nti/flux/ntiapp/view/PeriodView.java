package com.nti.flux.ntiapp.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nti.flux.ntiapp.R;
import com.nti.flux.ntiapp.model.Period;

public class PeriodView extends LinearLayout{
	private static final String TAG = PeriodView.class.getName();

	private TextView subject;
	
	public PeriodView(Context context){
		super(context);
	}
	
	public PeriodView(Context context, AttributeSet attrs){
		super(context, attrs);
		
		TypedArray a = context.getTheme().obtainStyledAttributes(
				attrs,
				R.styleable.NewsListItem,
				0,0);
		
		try{	
			subject.setText(a.getString(R.styleable.PeriodView_subject));
		}
		finally{
			a.recycle();
		}
	}
	
	public PeriodView(Context context, Period period, long breakMillis){
		super(context);
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.period, this, true);
		
		subject = (TextView)findViewById(R.id.subject);
		subject.setText(period.getSubject());
		Typeface contentFont = Typeface.createFromAsset(getContext().getAssets(), getResources().getString(R.string.font_content));
		subject.setTypeface(contentFont);
		
		TextView startTime = (TextView)findViewById(R.id.start_time);
		startTime.setText(period.getStartTimeString());
		startTime.setTypeface(contentFont);
		
		TextView endTime = (TextView)findViewById(R.id.end_time);
		endTime.setText(period.getEndTimeString());
		endTime.setTypeface(contentFont);
		
		//Set bottom margin proportionally to the next break's duration
		long value = breakMillis/50000;
		float marginPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, context.getResources().getDisplayMetrics());
		
		long heightRaw = (period.getEndTime().getTime() - period.getStartTime().getTime());
		long height = heightRaw/50000;
		float heightPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height, context.getResources().getDisplayMetrics());
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, (int)heightPx);
		params.setMargins(0, 0, 0, (int)marginPx);
		View v = view.findViewById(R.id.period_container);
		v.setLayoutParams(params);
	}
	public TextView getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject.setText(subject);
	}
}
