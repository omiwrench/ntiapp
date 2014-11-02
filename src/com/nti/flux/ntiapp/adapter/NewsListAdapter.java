package com.nti.flux.ntiapp.adapter;

import java.util.ArrayList;

import com.nti.flux.ntiapp.view.NewsListItem;

import android.app.Activity;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class NewsListAdapter extends ArrayAdapter<NewsListItem>{

	private ArrayList<NewsListItem> entries;
	
	public NewsListAdapter(Activity a, int textViewResouceId, ArrayList<NewsListItem> entries){
		super(a, textViewResouceId, entries);
		this.entries = entries;
	}

    public static class ViewHolder{
        public TextView item1;
        public TextView item2;
        public TextView item3;
        public TextView item4;
    }
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		NewsListItem vi = (NewsListItem)convertView;
		ViewHolder holder;
		
		if(vi == null){
			vi = new NewsListItem(getContext());
			holder = new ViewHolder();
			holder.item1 = vi.getTitleView();
			holder.item2 = vi.getContentView();
			holder.item3 = vi.getAuthorView();
			holder.item4 = vi.getDatePublishedView();
			vi.setTag(holder);
		}
		else
			holder = (ViewHolder) vi.getTag();
		
		final NewsListItem news = entries.get(position);
		if(news != null){
			holder.item1.setText(news.getTitle());
			holder.item2.setText(news.getContent());
			holder.item3.setText(news.getAuthor());
			holder.item4.setText(news.getDatePublished());
		}
		
		int paddingPx = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getContext().getResources().getDisplayMetrics());
		vi.setPadding(paddingPx, paddingPx, paddingPx, paddingPx/2);
		return vi;
	}

}
