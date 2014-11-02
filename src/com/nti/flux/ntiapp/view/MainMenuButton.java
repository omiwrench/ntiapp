package com.nti.flux.ntiapp.view;

import com.nti.flux.ntiapp.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainMenuButton extends RelativeLayout{
	private static final String TAG = MainMenuButton.class.getName();
	
	TextView text;
	Animation clickedAnim;
	
	public MainMenuButton(Context context) {
		super(context);
		text = new TextView(this.getContext());
		init();
	}
	
	public MainMenuButton(Context context, AttributeSet attrs){
		super(context, attrs);
		
		TypedArray a = context.getTheme().obtainStyledAttributes(
				attrs,
				R.styleable.MainMenuButton,
				0,0);
		
		try{
			String mText = a.getString(R.styleable.MainMenuButton_text);
			Log.d(TAG, mText);
			text = new TextView(this.getContext());
			text.setText(mText);
			text.setGravity(Gravity.CENTER);
			text.setTextColor(Color.WHITE);
			LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			text.setLayoutParams(lp);
			addView(text);
		}
		finally{
			a.recycle();
		}
		init();
	}
	
	private void init(){
		setBackgroundColor(getResources().getColor(R.color.mainmenu_button_color));
		setClickable(true);
		clickedAnim = AnimationUtils.loadAnimation(getContext(), R.anim.mainmenu_btn_touched_anim);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent e){
		super.onTouchEvent(e);
		
		if(e.getAction() == MotionEvent.ACTION_UP){
			setAlpha(1.0f);
			startAnimation(clickedAnim);
			
		}
		else if(e.getAction() == MotionEvent.ACTION_DOWN){
			setAlpha(0.2f);
			return true;
		}
		return false;
	}
}
