package com.nti.flux.ntiapp.adapter;

import java.util.ArrayList;

import com.nti.flux.ntiapp.view.InfoListItem;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class InfoListAdapter extends ArrayAdapter<InfoListItem>{

	private ArrayList<InfoListItem> entries;
	private Activity activity;
	
	public InfoListAdapter(Activity a, int textViewResouceId, ArrayList<InfoListItem> entries){
		super(a, textViewResouceId, entries);
		this.entries = entries;
		this.activity = a;
	}

    public static class ViewHolder{
        public TextView item1;
        public TextView item2;
    }
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		InfoListItem vi = (InfoListItem)convertView;
		ViewHolder holder;
		
		if(vi == null){
			vi = new InfoListItem(getContext());
			holder = new ViewHolder();
			holder.item1 = vi.getTitleView();
			holder.item2 = vi.getContentView();
			vi.setTag(holder);
		}
		else
			holder = (ViewHolder) vi.getTag();
		
		final InfoListItem info = entries.get(position);
		if(info != null){
			holder.item1.setText(info.getTitleView().getText());
			holder.item2.setText(info.getContentView().getText());
		}
		return vi;
	}

}
