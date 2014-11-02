package com.nti.flux.ntiapp.helper;

import android.content.Context;
import android.util.Log;

import com.nti.flux.ntiapp.dbhelper.ScheduleDbHelper;
import com.nti.flux.ntiapp.exception.NoScheduleFoundException;
import com.nti.flux.ntiapp.model.Person;
import com.nti.flux.ntiapp.model.Schedule;

public class ScheduleSearcher {
	
	private static final String TAG = ScheduleSearcher.class.getName();
	
	Context context;
	
	public ScheduleSearcher(Context c){
		this.context = c;
	}

	public Schedule getScheduleForPerson(Person p) throws NoScheduleFoundException{
		ScheduleDbHelper db = new ScheduleDbHelper(context);
		int scheduleId = Integer.parseInt(p.getScheduleId());
		Schedule s = db.getSchedule(scheduleId);
		if(s != null){
			return s;
		}
		else{
			throw new NoScheduleFoundException("No schedule found for person: " + p.getName() + ", in class: " + p.getClassName());
		}
	}
}
