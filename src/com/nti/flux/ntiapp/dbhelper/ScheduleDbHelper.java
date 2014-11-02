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

public class ScheduleDbHelper extends SQLiteOpenHelper{
	private static final String TAG = ScheduleDbHelper.class.getName();
	
	private static final String DATABASE_NAME = "schedule.db";
	private static final int DATABASE_VERSION = 6;
	
	private static final String PERIODS_TABLE_NAME = "periods";
	private static final String PERIOD_TABLE_CREATE = 
			"CREATE TABLE IF NOT EXISTS " + PERIODS_TABLE_NAME + " (" +
			"period_id" + " INTEGER PRIMARY KEY, " +
			"period_start" + " TEXT, " +
			"period_end" + " TEXT, " +
			"period_subject" + " TEXT, " +
			"period_teacher" + " TEXT, " +
			"period_room" + " TEXT);";
	
	private static final String SCHEDULES_TABLE_NAME = "schedules";
	private static final String SCHEDULES_TABLE_CREATE = 
			"CREATE TABLE IF NOT EXISTS " + SCHEDULES_TABLE_NAME + " (" +
			"schedule_id" + " INTEGER PRIMARY KEY, " +
			"schedule_class" + " TEXT);";
	
	private static final String SCHEDULE_PERIODS_TABLE_NAME = "schedule_periods";
	private static final String SCHEDULES_PERIODS_TABLE_CREATE = 
			"CREATE TABLE IF NOT EXISTS " + SCHEDULE_PERIODS_TABLE_NAME + " (" +
			"schedule_id" + " INTEGER, " +
			"period_id" + " INTEGER, " +
			"day" + " INTEGER);";
	
	private Context context;
	
	public ScheduleDbHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}
	
	 @Override
	 public void onCreate(SQLiteDatabase db){
		 Log.d(TAG, "Creating periods table");
		 db.execSQL(PERIOD_TABLE_CREATE);
		 db.execSQL(SCHEDULES_TABLE_CREATE);
		 db.execSQL(SCHEDULES_PERIODS_TABLE_CREATE);
	 }
	 
	 @Override
	 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		 db.execSQL("DROP TABLE IF EXISTS " + PERIODS_TABLE_NAME);
		 db.execSQL("DROP TABLE IF EXISTS " + SCHEDULES_TABLE_NAME);
		 db.execSQL("DROP TABLE IF EXISTS " + SCHEDULE_PERIODS_TABLE_NAME);
		 onCreate(db);
	 }
	 
	 public void addSchedule(Schedule schedule){
		 SQLiteDatabase db = this.getWritableDatabase();
		 
		 ContentValues values = new ContentValues();
		 values.put("schedule_class", schedule.getClassName());
		 values.put("schedule_id", schedule.getId());
		 
		 for(int i=0;i<schedule.getDays().size();i++){
			 Day day = schedule.getDays().get(i);
			 for(Period period : day.getPeriods()){
				 //Add period to periods table
				 values = new ContentValues();
				 values.put("period_start", period.getStartTimeString());
				 values.put("period_end", period.getEndTimeString());
				 values.put("period_subject", period.getSubject());
				 values.put("period_teacher", period.getTeacher());
				 values.put("period_room", period.getRoom());
				 
				 long id = db.insert(PERIODS_TABLE_NAME, null, values);
				 
				 values = new ContentValues();
				 values.put("schedule_id", schedule.getId());
				 values.put("period_id", id);
				 values.put("day", i);
				 db.insert(SCHEDULE_PERIODS_TABLE_NAME, null, values);
			 }
		 }
		 db.close();
	 }
	 
	 public Schedule getSchedule(int scheduleId){
		 Schedule schedule = new Schedule();
		 SQLiteDatabase db = this.getReadableDatabase();
		 
		 /*String classNames[] = new String[2];
		 classNames = className.split("-", 2);
		 String query = "SELECT * FROM "+ SCHEDULES_TABLE_NAME + " WHERE schedule_class LIKE \"%" + classNames[0] + "%\"";// AND schedule_class LIKE \"%" + classNames[1] + "%\"";
		 int scheduleId;
		 Cursor cursor = db.rawQuery(query, null);
				 //db.query(SCHEDULE_PERIODS_TABLE_NAME, new String[]{"schedule_class", "Word"}, "Word LIKE '?'", new String[]{className}, null, null, null, null);
		 
		 if(cursor.moveToFirst()){
			 scheduleId = cursor.getInt(0);
		 }
		 else{
			 Log.d(TAG, "Could not find any schedules for class: " + className);
			 cursor.close();
			 db.close();
			 return null;
		 }
		 
		 query = "SELECT COUNT() FROM " + SCHEDULE_PERIODS_TABLE_NAME;
		 cursor = db.rawQuery(query, null);
		 cursor.moveToFirst();*/
		 
		 String query = "SELECT * FROM " + SCHEDULE_PERIODS_TABLE_NAME + " WHERE schedule_id = " + scheduleId;
		 Cursor cursor = db.rawQuery(query, null);
		 if(cursor.moveToFirst()){
			 do{
				 int periodId = cursor.getInt(1);
				 int day = cursor.getInt(2);
				 String periodQuery = "SELECT * FROM " + PERIODS_TABLE_NAME + " WHERE period_id = " + periodId;
				 Cursor periodCursor = db.rawQuery(periodQuery, null);
				 if(periodCursor.moveToFirst()){
					 Period period = new Period(context);
					 period.setStartTimeAsString(periodCursor.getString(1));
					 period.setEndTimeAsString(periodCursor.getString(2));
					 period.setSubject(periodCursor.getString(3));
					 period.setTeacher(periodCursor.getString(4));
					 period.setRoom(periodCursor.getString(5));
					 
					 schedule.getDays().get(day).addPeriod(period);
				 }
			 }
			 while(cursor.moveToNext());
		 }
		 else{
			 Log.d(TAG, "no matches for id: " + scheduleId);
			 cursor.close();
			 db.close();
			 return null;
		 }		 
		 cursor.close();
		 db.close();
		 return schedule;
	 }
	 
	 public void clear(){
		 SQLiteDatabase db = this.getWritableDatabase();
		 db.execSQL("DROP TABLE IF EXISTS " + SCHEDULES_TABLE_NAME);
		 db.execSQL("DROP TABLE IF EXISTS " + SCHEDULE_PERIODS_TABLE_NAME);
		 onCreate(db);
	 }
}
