package com.nti.flux.ntiapp.fragment;

import com.nti.flux.ntiapp.R;
import com.nti.flux.ntiapp.helper.DatabaseUpdater;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DatabaseUpdatingFragment extends Fragment{
	
	private ViewGroup view;
	private static Runnable onFinish;
	private Activity a;

	public static DatabaseUpdatingFragment newInstance(Runnable r){
		DatabaseUpdatingFragment f = new DatabaseUpdatingFragment();
		onFinish = r;

		return f;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View v = inflater.inflate(R.layout.fragment_updating, container, false);
		this.view = (ViewGroup) v;
		
		Typeface titleFont = Typeface.createFromAsset(getActivity().getAssets(), getString(R.string.font_main_menu_button));
		Typeface contentType = Typeface.createFromAsset(getActivity().getAssets(), getResources().getString(R.string.font_content));
		
		TextView syncingTitle = (TextView)v.findViewById(R.id.text_syncing_title);
		syncingTitle.setTypeface(titleFont);
		
		TextView syncingDescription = (TextView)v.findViewById(R.id.text_syncing_description);
		syncingDescription.setTypeface(contentType);
		
		updateDatabase(getActivity());
		return v;
	}
	@Override
	public void onAttach(Activity a){
		super.onAttach(a);
		this.a = a;
	}
	
	public void updateDatabase(Context c){
		ProgressBar pb = new ProgressBar(c, null, android.R.attr.progressBarStyleHorizontal);
		pb.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		view.addView(pb);
		DatabaseUpdater du = new DatabaseUpdater(this.getActivity());
		if(a != null){
			du.updateDatabaseWithProgress(onFinish, pb);
		}
		else{
			du.updateDatabase();
		}
	}
}
