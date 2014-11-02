package com.nti.flux.ntiapp.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.nti.flux.ntiapp.R;
import com.nti.flux.ntiapp.adapter.NewsCategoryListAdapter;
import com.nti.flux.ntiapp.adapter.NewsListAdapter;
import com.nti.flux.ntiapp.dbhelper.NewsDbHelper;
import com.nti.flux.ntiapp.service.NtiService;
import com.nti.flux.ntiapp.view.NewsListItem;
import com.nti.flux.ntiapp.model.News;

public class NewsActivityWithCategories extends Activity{
	/*private static final String TAG = NewsActivity.class.getName();

	private ListView newsListView;
	List<String> categoriesList = new ArrayList<String>();
	ArrayList<NewsListItem> newsList;
	int currSelectedCategory = 0;
	
	@Override
	public void onCreate(Bundle state){
		super.onCreate(state);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_news);
		overridePendingTransition(R.anim.fade_in_accelerate, R.anim.fade_out_accelerate);
		
		newsListView = (ListView)findViewById(R.id.container_news);
		
		NewsDbHelper dbHelper = new NewsDbHelper(this);
		categoriesList = dbHelper.getRelevantCategories();
		ArrayAdapter<String> categoriesAdapter = new NewsCategoryListAdapter(this,
													android.R.layout.simple_list_item_1,
													categoriesList);
		ListView categoriesListView = (ListView)findViewById(R.id.drawer_news);
		categoriesListView.setAdapter(categoriesAdapter);
		categoriesListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int pos,
					long id) {
				String text = (String)((TextView)v).getText();
				showCategory(text);
			}
		});
		
		if(NtiService.updatingStudents)
			new WaitForDb(this).execute();
		
		showCategory("Allmänt");
	}
	
	private void showCategory(String title){
		newsList = new ArrayList<NewsListItem>();
		NewsDbHelper dbHelper = new NewsDbHelper(this);
		List<News> mNewsList = dbHelper.getNewsForCategory(title);
		for(News news : mNewsList){
			NewsListItem item = new NewsListItem(this);
			item.setTitle(news.getTitle());
			item.setContent(news.getContent());
			item.setAuthor(news.getAuthor());
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			item.setDatePublished(df.format(news.getPublished()));
			newsList.add(item);			
		}
		
		ArrayAdapter<NewsListItem> adapter = new NewsListAdapter(NewsActivityWithCategories.this,
												R.id.container_news,
												newsList);
		newsListView.setAdapter(adapter);
	}
	
	@Override
	public void onBackPressed() {
	    super.onBackPressed();
	    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}
	
	private class WaitForDb extends AsyncTask<Void, Void, Boolean>{
		
		private ProgressDialog pd;
		
		public WaitForDb(Context context){
			pd = new ProgressDialog(context);
		}
		
		protected void onPreExecute(){
			pd.setTitle("Ett ögonblick...");
			pd.setMessage("Uppdaterar lista på elever. Oroa dig inte, det händer inte varje gång.");
			pd.setCancelable(false);
			pd.show();
		}
		protected void onPostExecute(Boolean result){
			pd.dismiss();
		}
		@Override
		protected Boolean doInBackground(Void... arg0) {
			while(NtiService.updatingStudents){}
			return true;
		}
		
	}*/
}
