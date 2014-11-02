package com.nti.flux.ntiapp.model;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import com.nti.flux.ntiapp.R;

import android.content.Context;
import android.util.Log;

public class Period {
	private static final String TAG = Period.class.getName();
	
	private SimpleDateFormat df;
	
	private String subject = "";
	private String room = "";
	private String teacher = "";
	private Time startTime, endTime;
	private int day;
	
	private Context context;
	
	public Period(Context context){
		this.context = context;
		startTime = new Time(0);
		endTime = new Time(0);
		df = new SimpleDateFormat(this.context.getResources().getString(R.string.schedule_date_format), new Locale("sv", "SE"));
	}
	
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getStartTimeString() {
		return df.format(startTime);
	}
	public Time getStartTime(){
		return startTime;
	}
	public void setStartTime(Time startTime) {
		this.startTime = startTime;
	}
	public void setStartTimeAsString(String startTime){
		try{
			this.startTime = new Time(df.parse(startTime).getTime());	
		}
		catch(ParseException e){
			Log.e(TAG, "Could not parse time string: " + startTime);
		}
	}
	public String getEndTimeString() {
		return df.format(endTime);
	}
	public Time getEndTime(){
		return endTime;
	}
	public void setEndTime(Time endTime){
		this.endTime = endTime;
	}
	public void setEndTimeAsString(String endTime){
		try{
			this.endTime = new Time(df.parse(endTime).getTime());	
		}
		catch(ParseException e){
			Log.e(TAG, "Could not parse time string: " + endTime);
		}
	}
	public String getRoom() {
		return room;
	}
	public void setRoom(String room) {
		this.room = room;
	}
	public String getTeacher() {
		return teacher;
	}
	public void setTeacher(String teacher) {
		this.teacher = teacher;
	}
	
	public void setDay(int day){
		this.day = day;
	}
	public int getDay(){
		return day;
	}
}
