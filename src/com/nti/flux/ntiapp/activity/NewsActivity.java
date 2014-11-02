package com.nti.flux.ntiapp.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.nti.flux.ntiapp.R;
import com.nti.flux.ntiapp.adapter.NewsListAdapter;
import com.nti.flux.ntiapp.dbhelper.NewsDbHelper;
import com.nti.flux.ntiapp.view.NewsListItem;
import com.nti.flux.ntiapp.model.News;

public class NewsActivity extends Activity{
	private static final String TAG = NewsActivity.class.getName();

	private ListView newsListView;
	ArrayList<NewsListItem> newsList;
	
	@Override
	public void onCreate(Bundle state){
		super.onCreate(state);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_news);
		overridePendingTransition(R.anim.fade_in_accelerate, R.anim.fade_out_accelerate);
		
		newsListView = (ListView)findViewById(R.id.container_news);
		
		showCategory("Allmänt");
	}
	
	private void showCategory(String title){
		newsList = new ArrayList<NewsListItem>();
		NewsDbHelper dbHelper = new NewsDbHelper(this);
		List<News> mNewsList = dbHelper.getNews();
		for(News news : mNewsList){
			NewsListItem item = new NewsListItem(this);
			item.setTitle(news.getTitle());
			item.setContent(news.getContent());
			item.setAuthor(news.getAuthor());
			SimpleDateFormat df = new SimpleDateFormat("dd/MM");
			item.setDatePublished(df.format(news.getPublished()));
			newsList.add(item);			
		}
		
		ArrayAdapter<NewsListItem> adapter = new NewsListAdapter(NewsActivity.this,
												R.id.container_news,
												newsList);
		newsListView.setAdapter(adapter);
	}
	
	@Override
	public void onBackPressed() {
	    super.onBackPressed();
	    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}
	
	/*private class WaitForDb extends AsyncTask<Void, Void, Boolean>{
		
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
			while(NtiService.updatingDb){}
			return true;
		}
		
	} */
}
