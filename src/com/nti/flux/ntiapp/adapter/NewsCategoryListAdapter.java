package com.nti.flux.ntiapp.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nti.flux.ntiapp.R;
import com.nti.flux.ntiapp.model.Restaurant;

public class NewsCategoryListAdapter extends ArrayAdapter<String>{
	
	List<String> categories;

	public NewsCategoryListAdapter(Context context, int textViewResourceId, List<String> objects) {
		super(context, textViewResourceId, objects);
		this.categories = objects;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		TextView v = new TextView(getContext());
		Typeface tf = Typeface.createFromAsset(getContext().getAssets(), getContext().getResources().getString(R.string.font_main_menu_button));
		v.setTypeface(tf);
		v.setTextColor(Color.WHITE);
		v.setText(categories.get(position));
		int paddingPx = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getContext().getResources().getDisplayMetrics());
		v.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);
		return v;
	}
}
