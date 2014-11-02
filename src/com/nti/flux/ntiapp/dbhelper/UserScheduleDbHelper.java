package com.nti.flux.ntiapp.dbhelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.nti.flux.ntiapp.model.Day;
import com.nti.flux.ntiapp.model.Period;
import com.nti.flux.ntiapp.model.Schedule;

public class UserScheduleDbHelper extends SQLiteOpenHelper{
	private static final String TAG = UserScheduleDbHelper.class.getName();
	
	private static final String DATABASE_NAME = "user_schedule.db";
	private static final int DATABASE_VERSION = 5;
	
	private static final String PERIODS_TABLE_NAME = "user_periods";
	private static final String PERIOD_TABLE_CREATE = 
			"CREATE TABLE IF NOT EXISTS " + PERIODS_TABLE_NAME + " (" +
			"period_id" + " INTEGER PRIMARY KEY, " +
			"period_day" + " INTEGER, " +
			"period_start" + " TEXT, " +
			"period_end" + " TEXT, " +
			"period_subject" + " TEXT, " +
			"period_teacher" + " TEXT, " +
			"period_room" + " TEXT);";
	
	private Context context;
	
	public UserScheduleDbHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}
	
	 @Override
	 public void onCreate(SQLiteDatabase db){
		 Log.d(TAG, "Creating periods table");
		 db.execSQL(PERIOD_TABLE_CREATE);
	 }
	 
	 @Override
	 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		 db.execSQL("DROP TABLE IF EXISTS " + PERIODS_TABLE_NAME);
		 onCreate(db);
	 }
	 
	 public void addSchedule(Schedule schedule){
		 SQLiteDatabase db = this.getWritableDatabase();
		 //Removes all current periods, since the system should only handle one schedule at a time
		 db.delete(PERIODS_TABLE_NAME, null, null);
		 
		 for(int i=0;i<schedule.getDays().size();i++){
			 Day day = schedule.getDays().get(i);
			 for(Period period : day.getPeriods()){
				 //Add period to periods table
				 ContentValues values = new ContentValues();
				 values.put("period_day", i);
				 values.put("period_start", period.getStartTimeString());
				 values.put("period_end", period.getEndTimeString());
				 values.put("period_subject", period.getSubject());
				 values.put("period_teacher", period.getTeacher());
				 values.put("period_room", period.getRoom());
				 
				 db.insert(PERIODS_TABLE_NAME, null, values);
			 }
		 }
		 db.close();
	 }
	 
	 public Schedule getSchedule(){
		 Schedule schedule = new Schedule();
		 SQLiteDatabase db = this.getReadableDatabase();
		 
		 String queryPeriods = "SELECT * FROM "+ PERIODS_TABLE_NAME;
		 Cursor cursor = db.rawQuery(queryPeriods, null);
		 
		 if(cursor.moveToFirst()){
			 do{
				 Period period = new Period(context);
				 period.setStartTimeAsString(cursor.getString(2));
				 period.setEndTimeAsString(cursor.getString(3));
				 period.setSubject(cursor.getString(4));
				 period.setTeacher(cursor.getString(5));
				 period.setRoom(cursor.getString(6));
				 
				 int day = cursor.getInt(1);
				 
				 schedule.getDays().get(day).addPeriod(period);
			 }
			 while(cursor.moveToNext());
		 }
		 else{
			 Log.d(TAG, "Could not find any periods in database");
			 cursor.close();
			 db.close();
			 return null;
		 }
		 
		 cursor.close();
		 db.close();
		 return schedule;
	 }
}
