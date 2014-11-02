package com.nti.flux.ntiapp.model;

import java.util.ArrayList;
import java.util.List;

public class ScheduleSearchResult {
	private List<String> schedules = new ArrayList<String>();
	private List<String> names = new ArrayList<String>();
	private List<String> classes = new ArrayList<String>();
	public List<String> getSchedules() {
		return schedules;
	}
	public void setSchedules(List<String> schedules) {
		this.schedules = schedules;
	}
	public List<String> getNames() {
		return names;
	}
	public void setNames(List<String> names) {
		this.names = names;
	}
	public List<String> getClasses() {
		return classes;
	}
	public void setClasses(List<String> classes) {
		this.classes = classes;
	}
	
	
}
