package com.nti.flux.ntiapp.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.nti.flux.ntiapp.R;
import com.nti.flux.ntiapp.model.Restaurant;

public class RestaurantsListAdapter extends ArrayAdapter<Restaurant>{
	
	List<Restaurant> restaurants;
	LayoutInflater inflater;

	public RestaurantsListAdapter(Context context, int textViewResourceId, List<Restaurant> objects) {
		super(context, textViewResourceId, objects);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.restaurants = objects;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		View vi = convertView;
		if(vi == null){
			vi = inflater.inflate(R.layout.listitem_restaurant, null);
		}
		TextView v = (TextView) vi.findViewById(R.id.name_restaurant);
		v.setText(restaurants.get(position).getName());
		int paddingPx = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getContext().getResources().getDisplayMetrics());
		v.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);
		return vi;
	}
}
