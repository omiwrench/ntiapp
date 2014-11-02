package com.nti.flux.ntiapp.fragment;

import com.nti.flux.ntiapp.R;

import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView.FindListener;
import android.widget.LinearLayout;

public class MainMenuFragment extends Fragment{
	
	private static final String TAG = MainMenuFragment.class.getName();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		getActivity().getFragmentManager().beginTransaction().hide(this).commit();
		return inflater.inflate(R.layout.fragment_mainmenu, container, false);
	}
	
	public void showButtons(){
		getActivity().getFragmentManager().beginTransaction().show(this).commit();
		LinearLayout view = (LinearLayout) this.getView();
		int children = view.getChildCount();
		for(int i=0;i<children;i++){
			Animation anim = AnimationUtils.loadAnimation(this.getActivity(), R.anim.fade_in_accelerate);
			Log.d(TAG, "loop number: " + Integer.valueOf(i+1));
			final View child = (View) view.getChildAt(i);
			anim.setStartOffset(i*100);
			child.startAnimation(anim);
		}
	}
}
