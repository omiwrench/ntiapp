package com.nti.flux.ntiapp.dbhelper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.nti.flux.ntiapp.model.Restaurant;
import com.nti.flux.ntiapp.model.Restaurants;

public class RestaurantsDbHelper extends SQLiteOpenHelper{
	private static final String TAG = RestaurantsDbHelper.class.getName();
	
	private static final String DATABASE_NAME = "restaurants.db";
	private static final int DATABASE_VERSION = 3;
	
	private static final String RESTAURANTS_TABLE_NAME = "restaurants";
	private static final String RESTAURANTS_TABLE_CREATE = 
			"CREATE TABLE IF NOT EXISTS " + RESTAURANTS_TABLE_NAME + " ("
					+ "restaurant_id" + " INTEGER PRIMARY KEY, " 
					+ "restaurant_name" + " TEXT, "
					+ "restaurant_description" + " TEXT, "
					+ "latitude" + " REAL, "
					+ "longitude" + " REAL, "
					+ "extra_cost" + " INTEGER);";
	
	public RestaurantsDbHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public void setRestaurants(Restaurants restaurants){
		Log.d(TAG, "setting restaurants");
		SQLiteDatabase db = this.getWritableDatabase();
		removeRestaurants(db);
		onCreate(db);
		if(restaurants != null && restaurants.getRestaurants() != null){
			Log.d(TAG, "number of restaurants in insert: " + restaurants.getRestaurants().size());
			for(Restaurant r : restaurants.getRestaurants()){
				ContentValues values = new ContentValues();
				values.put("restaurant_name", r.getName());
				values.put("restaurant_description", r.getDescription());
				values.put("latitude", "" + r.getLat());
				values.put("longitude", "" + r.getLon());
				if(r.isExtra())
					values.put("extra_cost", 1);
				else
					values.put("extra_cost", 0);
				db.insert(RESTAURANTS_TABLE_NAME, null, values);
				Log.d(TAG, "inserting:" + values.get("latitude"));
				Log.d(TAG, "test: " + r.getLat());
			}
		}
	}
	
	public Restaurants getRestaurants(){
		Log.d(TAG, "getting restaurants");
		List<Restaurant> restaurants = new ArrayList<Restaurant>();
		SQLiteDatabase db = this.getReadableDatabase();
		
		String query = "SELECT * FROM " + RESTAURANTS_TABLE_NAME;
		Cursor cursor = db.rawQuery(query, null);
		cursor.moveToFirst();
		do{
			Restaurant r = new Restaurant();
			r.setName(cursor.getString(1));
			r.setDescription(cursor.getString(2));
			r.setLat(BigDecimal.valueOf(cursor.getDouble(3)));
			r.setLon(BigDecimal.valueOf(cursor.getDouble(4)));
			r.setExtra(cursor.getInt(5) == 1);
			restaurants.add(r);
		}
		while(cursor.moveToNext());
		
		db.close();
		Restaurants r = new Restaurants();
		r.setRestaurants(restaurants);
		return r;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db){
		Log.i(TAG, "Creating news database");
		db.execSQL(RESTAURANTS_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		removeRestaurants(db);
		onCreate(db);
	}
	
	private void removeRestaurants(SQLiteDatabase db){
		db.execSQL("DROP TABLE IF EXISTS " + RESTAURANTS_TABLE_NAME);
	}
}
