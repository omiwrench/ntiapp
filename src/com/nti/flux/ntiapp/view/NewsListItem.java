package com.nti.flux.ntiapp.view;

import com.nti.flux.ntiapp.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NewsListItem extends LinearLayout{
	private static final String TAG = NewsListItem.class.getName();
	
	private TextView title;
	private TextView content;
	private TextView datePublished;
	private TextView author;

	public NewsListItem(Context context){
		super(context);
		title = new TextView(this.getContext());
		content = new TextView(this.getContext());
		author = new TextView(this.getContext());
		datePublished = new TextView(this.getContext());
		init();
	}
	
	public NewsListItem(Context context, AttributeSet attrs){
		super(context, attrs);
		
		TypedArray a = context.getTheme().obtainStyledAttributes(
				attrs,
				R.styleable.NewsListItem,
				0,0);
		
		try{
			title = new TextView(context);
			content = new TextView(context);
			author = new TextView(context);
			datePublished = new TextView(context);
			
			title.setText(a.getString(R.styleable.NewsListItem_title));
			content.setText(a.getString(R.styleable.NewsListItem_content));
			author.setText(a.getString(R.styleable.NewsListItem_author));
			datePublished.setText(a.getString(R.styleable.NewsListItem_date));
		}
		finally{
			a.recycle();
		}
		
		init();
	}
	
	private void init(){
		setOrientation(VERTICAL);
		
		Typeface titleFont = Typeface.createFromAsset(getContext().getAssets(), getResources().getString(R.string.font_main_menu_button));
		title.setTypeface(titleFont);
		LayoutParams textLp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		textLp.setMargins((int)getResources().getDimension(R.dimen.news_badge_left_margin), 0, 0, 0);
		title.setLayoutParams(textLp);
		title.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
		title.setTextColor(Color.WHITE);
		title.setTextSize(getResources().getDimension(R.dimen.news_title_text_size));
		
		LinearLayout titleBadge = new LinearLayout(this.getContext());
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		lp.setMargins(0, 0, 0, (int)getResources().getDimension(R.dimen.news_badge_margin));
		titleBadge.setLayoutParams(lp);
		titleBadge.setBackgroundResource(R.drawable.news_item_badge);
		titleBadge.addView(title);
		int paddingDp = (int)getResources().getDimension(R.dimen.news_badge_margin);
		titleBadge.setPadding(paddingDp, paddingDp, paddingDp, paddingDp);
		addView(titleBadge);
		
		Typeface contentType = Typeface.createFromAsset(getContext().getAssets(), getResources().getString(R.string.font_content));
		content.setLayoutParams(textLp);
		content.setTextColor(Color.WHITE);
		content.setGravity(Gravity.LEFT | Gravity.TOP);
		content.setTextSize(getResources().getDimension(R.dimen.news_content_text_size));
		content.setTypeface(contentType);
		paddingDp = (int)getResources().getDimension(R.dimen.news_content_padding);
		content.setPadding(paddingDp, paddingDp, paddingDp, paddingDp);
		
		LinearLayout contentBlock = new LinearLayout(this.getContext());
		contentBlock.setOrientation(HORIZONTAL);
		contentBlock.setLayoutParams(lp);
		contentBlock.setBackgroundColor(getResources().getColor(R.color.mainmenu_button_color));
		contentBlock.addView(content);
		addView(contentBlock);
		
		RelativeLayout infoBlock = new RelativeLayout(this.getContext());
		RelativeLayout.LayoutParams rLp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		infoBlock.setLayoutParams(rLp);
		infoBlock.setBackgroundResource(R.drawable.news_item_info);
		infoBlock.setPadding(paddingDp, paddingDp, paddingDp, paddingDp);
		addView(infoBlock);
		
		rLp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		rLp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		rLp.addRule(RelativeLayout.CENTER_VERTICAL);
		Typeface smallType = Typeface.createFromAsset(getContext().getAssets(), getResources().getString(R.string.font_small_type));
		author.setLayoutParams(rLp);
		author.setTextColor(getResources().getColor(R.color.news_info_text_color));
		author.setTypeface(smallType);
		author.setTextSize(getResources().getDimension(R.dimen.news_author_text_size));
		infoBlock.addView(author);
		
		rLp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		rLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		rLp.addRule(RelativeLayout.CENTER_VERTICAL);
		datePublished.setLayoutParams(rLp);
		datePublished.setTextColor(getResources().getColor(R.color.news_info_text_color));
		datePublished.setTextSize(getResources().getDimension(R.dimen.news_date_text_size));
		datePublished.setGravity(Gravity.RIGHT);
		datePublished.setTypeface(smallType);
		infoBlock.addView(datePublished);
	}
	
	public String getTitle(){
		return (String) title.getText();
	}
	public void setTitle(String title){
		this.title.setText(title);
	}
	public String getContent(){
		return (String) content.getText();
	}
	public void setContent(String content){
		this.content.setText(content);
	}
	public String getAuthor(){
		return (String) author.getText();
	}
	public void setAuthor(String author){
		this.author.setText(author);
	}
	public String getDatePublished() {
		return (String) datePublished.getText();
	}
	public void setDatePublished(String datePublished) {
		this.datePublished.setText(datePublished);
	}
	public TextView getTitleView(){
		return title;
	}
	public TextView getContentView(){
		return content;
	}
	public TextView getAuthorView(){
		return author;
	}
	public TextView getDatePublishedView(){
		return datePublished;
	}
}
