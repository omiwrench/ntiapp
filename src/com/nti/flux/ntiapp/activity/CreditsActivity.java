package com.nti.flux.ntiapp.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.nti.flux.ntiapp.R;

public class CreditsActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_credits);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		
		TextView title = (TextView) findViewById(R.id.credits_title);
		TextView titleSub = (TextView) findViewById(R.id.credits_title_sub);
		TextView description = (TextView) findViewById(R.id.credits_description);
		
		Typeface tf = Typeface.createFromAsset(getAssets(), getString(R.string.font_main_menu_button));
		Typeface cf = Typeface.createFromAsset(getAssets(), getString(R.string.font_content));
		
		title.setTypeface(tf);
		titleSub.setTypeface(cf);
		description.setTypeface(cf);
	}
}
