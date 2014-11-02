package com.nti.flux.ntiapp.dbhelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.nti.flux.ntiapp.model.News;

public class NewsDbHelper extends SQLiteOpenHelper{
	private static final String TAG = NewsDbHelper.class.getName();
	
	private static final String DATABASE_NAME = "news.db";
	private static final int DATABASE_VERSION = 3;
	
	private static final String CATEGORIES_TABLE_NAME = "categories";
	private static final String CATEGORIES_TABLE_CREATE = 
			"CREATE TABLE IF NOT EXISTS " + CATEGORIES_TABLE_NAME + " ("
			+ "category_id" + " INTEGER, "
			+ "category_title" + " TEXT);";
	
	private static final String NEWS_TABLE_NAME = "news";
	private static final String NEWS_TABLE_CREATE = 
			"CREATE TABLE IF NOT EXISTS " + NEWS_TABLE_NAME + " ("
			+ "news_id" + " INTEGER, " 
			+ "news_title" + " TEXT, "
			+ "news_content" + " TEXT, "
			+ "news_published" + " DATE, "
			+ "news_author" + " TEXT);";

	public NewsDbHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public void setNews(List<News> newsList){
		SQLiteDatabase db = this.getWritableDatabase();
		removeNews(db);
		onCreate(db);
		if(newsList != null && !newsList.isEmpty()){
			for(News news : newsList){
				ContentValues values = new ContentValues();
				values.put("news_id", news.getId());
				values.put("news_title", news.getTitle());
				values.put("news_content", news.getContent());
				SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd");
				if(news.getPublished() != null)
					values.put("news_published", d.format(news.getPublished()));
				else
					Log.e(TAG, "published date was null");
				values.put("news_author", news.getAuthor());
				db.insert(NEWS_TABLE_NAME, null, values);
			}
		}
		db.close();
	}
	
	public List<News> getNews(){
		List<News> newsList = new ArrayList<News>();
		SQLiteDatabase db = this.getReadableDatabase();
		
		String query = "SELECT * FROM " + NEWS_TABLE_NAME;
		Cursor cursor = db.rawQuery(query, null);
		cursor.moveToFirst();
		do{
			News news = new News();
			news.setId(cursor.getInt(0));
			news.setTitle(cursor.getString(1));
			news.setContent(cursor.getString(2));
			news.setAuthor(cursor.getString(4));
			SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd");
			try{
				news.setPublished(d.parse(cursor.getString(3)));
			}
			catch(ParseException e){
				Log.e(TAG, "Could not parse news publish date");
			}
			catch(NullPointerException e){}
			newsList.add(news);
		}
		while(cursor.moveToNext());
		
		db.close();
		return newsList;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i(TAG, "Creating news database");
		db.execSQL(CATEGORIES_TABLE_CREATE);
		db.execSQL(NEWS_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		removeNews(db);
		onCreate(db);
	}
	
	private void removeNews(SQLiteDatabase db){
		String removeNews = "DROP TABLE IF EXISTS " + NEWS_TABLE_NAME;
		db.execSQL(removeNews);
	}
}
