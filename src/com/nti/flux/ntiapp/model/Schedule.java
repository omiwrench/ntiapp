package com.nti.flux.ntiapp.model;

import java.util.ArrayList;
import java.util.List;

public class Schedule {
	
	public static final int DAY_MONDAY = 0,
							 DAY_TUESDAY = 1,
							 DAY_WEDNESDAY = 2,
							 DAY_THURSDAY = 3,
							 DAY_FRIDAY = 4;
							 
	private List<Day> days;
	private String className;
	private int id;
	
	
	public Schedule(){
		days = new ArrayList<Day>();
		for(int i=0;i<5;i++){
			days.add(new Day());
		}
	}
	
	public void addDay(Day day){
		days.add(day);
	}
	public void addDayAt(int index, Day day){
		days.add(index, day);
	}
	public List<Day> getDays(){
		return days;
	}
	public void setDays(List<Day> days){
		this.days = days;
	}
	public void addPeriodToDay(Period period, int day){
		days.get(day).addPeriod(period);
	}
	
	public String getClassName(){
		return className;
	}
	public void setClassName(String className){
		this.className = className;
	}
	public void setId(int id){
		this.id = id;
	}
	public int getId(){
		return id;
	}
}
