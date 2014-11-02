package com.nti.flux.ntiapp.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nti.flux.ntiapp.R;
import com.nti.flux.ntiapp.model.Restaurant;

public class RestaurantInfoPanelFragment extends Fragment{
	private static final String TAG = RestaurantInfoPanelFragment.class.getName();
	
	public static RestaurantInfoPanelFragment newInstance(){
		RestaurantInfoPanelFragment f = new RestaurantInfoPanelFragment();
		return f;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state){
		View root = inflater.inflate(R.layout.fragment_restaurant_details, container, false);
		return root;
	}
	
	public void restaurantChanged(Restaurant r){
		
		Typeface titleFont = Typeface.createFromAsset(getActivity().getAssets(), getString(R.string.font_main_menu_button));
		Typeface contentType = Typeface.createFromAsset(getActivity().getAssets(), getResources().getString(R.string.font_content));
		
		TextView title = (TextView)this.getView().findViewById(R.id.restaurant_title);
		title.setText(r.getName());
		title.setTypeface(titleFont);
		
		TextView details = (TextView)this.getView().findViewById(R.id.restaurant_details);
		details.setText(r.getDescription());
		details.setTypeface(contentType);
	}
}
