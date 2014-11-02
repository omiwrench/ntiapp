package com.nti.flux.ntiapp.fragment;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nti.flux.ntiapp.R;
import com.nti.flux.ntiapp.service.NtiService;

public class SyncingFragment extends Fragment{
	
	Runnable onFinish;
	Activity a;

	public static SyncingFragment newInstance(){
		SyncingFragment f = new SyncingFragment();

		return f;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View v = inflater.inflate(R.layout.fragment_syncing, container, false);
		
		Typeface titleFont = Typeface.createFromAsset(getActivity().getAssets(), getString(R.string.font_main_menu_button));
		Typeface contentType = Typeface.createFromAsset(getActivity().getAssets(), getResources().getString(R.string.font_content));
		
		TextView syncingTitle = (TextView)v.findViewById(R.id.text_syncing_title);
		syncingTitle.setTypeface(titleFont);
		
		TextView syncingDescription = (TextView)v.findViewById(R.id.text_syncing_description);
		syncingDescription.setTypeface(contentType);
		
		new WaitForUpdate(onFinish).execute();
		return v;
	}
	@Override
	public void onAttach(Activity a){
		super.onAttach(a);
		this.a = a;
	}
	public void setOnFinish(Runnable onFinish){
		this.onFinish = onFinish;
	}
	
	private class WaitForUpdate extends AsyncTask<Void, Void, Void>{

		Runnable onFinish;
		
		public WaitForUpdate(Runnable onFinish){
			this.onFinish = onFinish;
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			while(NtiService.updatingDatabase){
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {}
			}
			return null;
		}
		
		protected void onPostExecute(Void unused){
			if(a != null)
				onFinish.run();
		}
		
	}
}
