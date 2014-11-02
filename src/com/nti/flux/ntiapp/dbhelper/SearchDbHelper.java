package com.nti.flux.ntiapp.dbhelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.nti.flux.ntiapp.model.Person;

public class SearchDbHelper extends SQLiteOpenHelper{
	private static final String TAG = SearchDbHelper.class.getName();

	private static final int TYPE_STUDENT = 0, TYPE_TEACHER = 1, TYPE_ROOM = 2;
	
	private static final String DATABASE_NAME = "search.db";
	private static final int DATABASE_VERSION = 2;
	
	private static final String STUDENTS_TABLE_NAME = "students";
	private static final String STUDENTS_TABLE_CREATE =
			"CREATE TABLE IF NOT EXISTS " + STUDENTS_TABLE_NAME + " (" +
			"student_id" + " INTEGER PRIMARY KEY, "+
			"student_name" + " TEXT, " + 
			"student_class" + " TEXT, " +
			"student_schedule_id" + " TEXT);";
	
	private static final String TEACHERS_TABLE_NAME = "teachers";
	private static final String TEACHERS_TABLE_CREATE =
			"CREATE TABLE IF NOT EXISTS " + TEACHERS_TABLE_NAME + " (" +
			"teacher_id" + " INTEGER PRIMARY KEY, "+
			"teacher_name" + " TEXT, " + 
			"teacher_class" + " TEXT, " +
			"teacher_schedule_id" + " TEXT);";
	
	public SearchDbHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db){
		Log.d(TAG, "Creating students table");
		db.execSQL(STUDENTS_TABLE_CREATE);
		db.execSQL(TEACHERS_TABLE_CREATE);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		db.execSQL("DROP TABLE IF EXISTS " + STUDENTS_TABLE_NAME + "; " +
				   "DROP TABLE IF EXISTS " + TEACHERS_TABLE_NAME);
		onCreate(db);
	}
	
	public void addStudents(List<Person> students){
		addItems(students, TYPE_STUDENT);
	}
	
	public List<Person> getStudentsByName(String name){
		return getItemsByName(name, TYPE_STUDENT);
	}
	
	public void addTeachers(List<Person> teachers){
		addItems(teachers, TYPE_TEACHER);
	}
	
	public List<Person> getTeachersByName(String name){
		return getItemsByName(name, TYPE_TEACHER);
	}
	
	private List<Person> getItemsByName(String name, int type){
		
		//Check if input contains space, i.e "Felix Andersson", since it's saved as "Andersson Felix" in the database, and will thus not return any results
		List<String> searchStrings = new ArrayList<String>();
		// "\\s" = regex for whitespace
		Pattern p = Pattern.compile("\\s");
		Matcher m = p.matcher(name);
		boolean split = m.find();
		if(split){
			searchStrings = new ArrayList<String>(Arrays.asList(name.split("\\s", 5)));
		}
		else{
			searchStrings.add(name);
		}
		
		switch(type){
		case TYPE_STUDENT:{
			SQLiteDatabase db = this.getReadableDatabase();
			List<Person> students = new ArrayList<Person>();
			String query = "";
			if(!split)
				query = "SELECT * FROM " + STUDENTS_TABLE_NAME + " WHERE student_name LIKE '%" + name + "%'";
			else{
				query = "SELECT * FROM " + STUDENTS_TABLE_NAME + " WHERE";
				int i = 0;
				while(i<searchStrings.size()-1){
					query = query  + " student_name LIKE '%" + searchStrings.get(i) + "%' AND";
					i++;
				}
				query = query + "  student_name LIKE '%" + searchStrings.get(i) + "%'";
			}
			Cursor cursor = db.rawQuery(query, null);
			
			if(cursor.moveToFirst()){
				do{
					Person student = new Person();
					student.setName(cursor.getString(1));
					student.setClassName(cursor.getString(2));
					student.setScheduleId(cursor.getString(3));
					students.add(student);
				}while(cursor.moveToNext());
			}
			else{
				Log.d(TAG, "No students found in database");
			}
			cursor.close();
			db.close();
			return students;
		}
		case TYPE_TEACHER:{
			SQLiteDatabase db = this.getReadableDatabase();
			List<Person> teachers = new ArrayList<Person>();
			String query = "SELECT * FROM " + TEACHERS_TABLE_NAME + " WHERE teacher_name LIKE '%" + name + "%';";
			Cursor cursor = db.rawQuery(query, null);
			
			if(cursor.moveToFirst()){
				do{
					Person teacher = new Person();
					teacher.setName(cursor.getString(1));
					teacher.setClassName(cursor.getString(2));
					teacher.setScheduleId(cursor.getString(3));
					Log.d(TAG, "id: " + cursor.getString(3));
					teachers.add(teacher);
				}while(cursor.moveToNext());
			}
			else{
				Log.d(TAG, "No students found in database");
			}
			cursor.close();
			db.close();
			return teachers;
		}
		case TYPE_ROOM:{
			//Get by room
		}
		default:
			Log.e(TAG, "unknown type");
			return null;
		}
	}
	
	private void addItems(List<Person> items, int type){
		switch(type){
		case TYPE_STUDENT:{
			SQLiteDatabase db = this.getWritableDatabase();
			db.execSQL("DROP TABLE IF EXISTS " + STUDENTS_TABLE_NAME);
			onCreate(db);
			for(Person student : items){
				ContentValues values = new ContentValues();
				values.put("student_name", student.getName());
				values.put("student_class", student.getClassName());
				values.put("student_schedule_id", student.getScheduleId());
				db.insert(STUDENTS_TABLE_NAME, null, values);	
			}
			db.close();
			break;
		}
		case TYPE_TEACHER:{
			SQLiteDatabase db = this.getWritableDatabase();
			db.execSQL("DROP TABLE IF EXISTS " + TEACHERS_TABLE_NAME);
			onCreate(db);
			for(Person teacher : items){
				ContentValues values = new ContentValues();
				values.put("teacher_name", teacher.getName());
				values.put("teacher_class", teacher.getClassName());
				values.put("teacher_schedule_id", teacher.getScheduleId());
				db.insert(TEACHERS_TABLE_NAME, null, values);	
			}
			db.close();
			break;
		}
		case TYPE_ROOM:{
			//Insert rooms
			break;
		}
		default:
			Log.e(TAG, "unknown type");
		}
	}
	
	public void clear(){
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("DROP TABLE IF EXISTS " + STUDENTS_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + TEACHERS_TABLE_NAME);
		onCreate(db);
	}
}
