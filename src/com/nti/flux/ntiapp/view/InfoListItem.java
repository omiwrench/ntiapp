package com.nti.flux.ntiapp.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nti.flux.ntiapp.R;

public class InfoListItem extends RelativeLayout{

	private TextView title;
	private TextView content;
	
	public InfoListItem(Context context) {
		super(context);
		init();
	}
	
	public InfoListItem(Context context, AttributeSet attrs){
		super(context, attrs);
		
		init();
		
		TypedArray a = context.getTheme().obtainStyledAttributes(
				attrs,
				R.styleable.InfoListItem,
				0,0);
		try{
			title.setText(a.getString(R.styleable.InfoListItem_info_title));
			content.setText(a.getString(R.styleable.InfoListItem_info_content));
		}
		finally{
			a.recycle();
		}
	}
	public void init(){
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.listitem_info, this, true);
		
		title = (TextView)findViewById(R.id.title);
		content = (TextView)findViewById(R.id.content);
	}
	
	public TextView getTitleView(){
		return title;
	}
	public void setTitle(String title){
		this.title.setText(title);
	}
	public TextView getContentView(){
		return content;
	}
	public void setContent(String content){
		this.content.setText(content);
	}
}
