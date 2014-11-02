package com.nti.flux.ntiapp.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolException;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;

import com.nti.flux.ntiapp.activity.SettingsActivity;
import com.nti.flux.ntiapp.exception.InvalidSchoolsoftAuthException;
import com.nti.flux.ntiapp.exception.ScheduleRetreivalFailedException;
import com.nti.flux.ntiapp.fragment.SearchSchedulesFragment.LoadScheduleTask;
import com.nti.flux.ntiapp.fragment.SearchSchedulesFragment.SearchTask;
import com.nti.flux.ntiapp.model.News;
import com.nti.flux.ntiapp.model.Period;
import com.nti.flux.ntiapp.model.Person;
import com.nti.flux.ntiapp.model.Schedule;
import com.nti.flux.ntiapp.model.ScheduleSearchResult;

public class ImportSchoolsoft implements RedirectHandler{
	private static final String TAG = ImportSchoolsoft.class.getName();
	private static final String T = "SETUP TEST";
	
	private static final int PERSON_TYPE_STUDENT = 0, PERSON_TYPE_TEACHER = 1;
	private static final String SCHEDULE_BASE_URL = "https://sms7.schoolsoft.se/nti/jsp/student/right_student_schedule.jsp";
	private static final String URL_GET_CATEGORIES = "https://sms7.schoolsoft.se/nti/jsp/student/right_student_news.jsp?type=1&action=viewold&category=-1";
	private static final String URL_BASE_NEWS = "https://sms7.schoolsoft.se/nti/jsp/student/right_student_news.jsp";
	
	private Context context;
	private String username;
	private String password;
	private int usertype;
	
	private int check;
	
	DefaultHttpClient httpclient = new DefaultHttpClient();
	public ImportSchoolsoft(Context context) throws InvalidSchoolsoftAuthException{
		Log.d(T, "Instancing ImportSchoolsoft");
		this.context = context;
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		usertype = 1;
		String u = sharedPref.getString(SettingsActivity.KEY_SCHOOLSOFT_USERNAME, "");
		String p = sharedPref.getString(SettingsActivity.KEY_SCHOOLSOFT_PASSWORD, "");
		if(u != null && u != "" && p != null && p!= ""){
			username = u;
			password = p;
		}
		else{
			throw new InvalidSchoolsoftAuthException("Could not retrieve schoolsoft authentication information from settings.");
		}
	}
	public ImportSchoolsoft(Context context, String username, String password, int usertype){
    	this.username = username;
    	this.password = password;
    	this.usertype = usertype;
    	this.context = context;
    }
	
	public boolean confirmLogin(){
		try{
			Log.d(T, "TESTING LOGIN");
			//TODO check if this is necessary
			httpclient.setRedirectHandler(this);
			String url = "https://sms7.schoolsoft.se/nti/jsp/Login.jsp";
			//All data som ska skickas för att kunna logga in
			HttpPost httppost = new HttpPost(url);
			httppost.setHeader("User-Agent", "Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6");
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        nameValuePairs.add(new BasicNameValuePair("action", "login"));
	        nameValuePairs.add(new BasicNameValuePair("usertype", "" + usertype));
	        nameValuePairs.add(new BasicNameValuePair("ssusername", username));
	        nameValuePairs.add(new BasicNameValuePair("sspassword", password));
	        nameValuePairs.add(new BasicNameValuePair("button", "Logga+in"));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	        HttpResponse response = httpclient.execute(httppost);
	        
			//Checks for warning messages
			check = checkIfSuccess(response.getEntity().getContent());
	        if(check != 0){
	        	Log.e(TAG, "Login to schoolsoft failed.");
	        	return false;
	    	}
			
			return true;	
		}
		catch(Exception e){
			Log.e(TAG, "Exception thrown while logging in to schoolsoft: " + e.getMessage());
		}
		return false;
	}
	
	public List<Cookie> login(){
		try{
			//TODO check if this is necessary
			httpclient.setRedirectHandler(this);
			String url = "https://sms7.schoolsoft.se/nti/jsp/Login.jsp";
			//All data som ska skickas för att kunna logga in
			HttpPost httppost = new HttpPost(url);
			httppost.setHeader("User-Agent", "Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6");
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        nameValuePairs.add(new BasicNameValuePair("action", "login"));
	        nameValuePairs.add(new BasicNameValuePair("usertype", "" + usertype));
	        nameValuePairs.add(new BasicNameValuePair("ssusername", username));
	        nameValuePairs.add(new BasicNameValuePair("sspassword", password));
	        nameValuePairs.add(new BasicNameValuePair("button", "Logga+in"));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	        HttpResponse response = httpclient.execute(httppost);
	        
			//Checks for warning messages
			check = checkIfSuccess(response.getEntity().getContent());
	        if(check != 0){
	        	Log.e(TAG, "Login to schoolsoft failed.");
	        	return null;
	    	}
	        
	        //Save cookies so we're free to navigate schoolsoft
	        List<Cookie> cookies = httpclient.getCookieStore().getCookies();
			
			return cookies;	
		}
		catch(Exception e){
			Log.e(TAG, "Exception thrown while logging in to schoolsoft: " + e.getMessage());
		}
		return null;
	}
	
	//Eternal damnation falls on ye who runs this on the UI thread
    public Schedule getScheduleForAppUser() {
    	Log.d(T, "GETTING USER SCHEDULE");
    	
		Schedule schedule = new Schedule();
    	
    	try{		
    		login();
            HttpPost httppost2 = new HttpPost(SCHEDULE_BASE_URL);
            HttpResponse response2 = httpclient.execute(httppost2);
            Document sceDoc = Jsoup.parse(response2.getEntity().getContent(), null, SCHEDULE_BASE_URL);
            
			//Find all elements with "schedule" class, i.e every period element in the displayed schedule
            Elements sceEle = sceDoc.getElementsByClass("schedule");
            Vector<String>temps = new Vector<String>();
			//Loops through all periods in the schedule
            for(int i=0;i<sceEle.size();i++){
				//Fetches the ajax url for every period
            	String scheduleUrl = sceEle.get(i).attr("href").toString();
            	String ajax = scheduleUrl.substring(scheduleUrl.indexOf('?') + 1, scheduleUrl.length() - 14);
            	temps.add(ajax);
            }
            
            //Loops through all the "pages" for the periods, i.e the one you get redirected to with the ajax url
            for(int j = 0; j < temps.size(); j++){
            	
            	Period period = new Period(context);
            	
		    	HttpPost httppost3 = new HttpPost("https://sms7.schoolsoft.se/nti/jsp/student/right_student_schedule_ajax.jsp?" + temps.elementAt(j));
	            HttpResponse response3 = httpclient.execute(httppost3);
			    
			    Document doc = Jsoup.parse(response3.getEntity().getContent(), "ISO-8859-1","https://sms7.schoolsoft.se/nti/jsp/student/right_student_schedule_ajax.jsp?" + temps.elementAt(j));
			    
			    period = parsePeriodDoc(doc);
			    schedule.addPeriodToDay(period, period.getDay());

		    }
        }
    	catch (IOException e){
    		check = 3;
    	}
    	catch (IllegalArgumentException e){
    		check = 2;
    	}
    	return schedule;
    	
	}
    
	public ScheduleSearchResult getScheduleByName(String name, SearchTask task){
		ScheduleSearchResult result = new ScheduleSearchResult();
		List<String> scheduleUrls = new ArrayList<String>();
		List<String> names = new ArrayList<String>();
		List<String> classes = new ArrayList<String>();
		
		try{
    		login();
            HttpPost httppost2 = new HttpPost();
            HttpResponse response2 = httpclient.execute(httppost2);
            Document sceDoc = Jsoup.parse(response2.getEntity().getContent(),null,"https://sms7.schoolsoft.se/nti/jsp/student/right_student_class.jsp");
            
            Element parent = sceDoc.getElementById("contAll");
            Elements classesItems = parent.getElementsByTag("li");
            for(int i=0;i<classesItems.size();i++){
            	Element element = classesItems.get(i);
            	String className = element.getElementsByTag("a").get(0).html();
            	String subUrl = element.getElementsByTag("a").get(0).attr("href").toString();
            	HttpPost httpPost4 = new HttpPost("https://sms7.schoolsoft.se/nti/jsp/student/" + subUrl);
            	HttpResponse response4 = httpclient.execute(httpPost4);
            	Document classDoc = Jsoup.parse(response4.getEntity().getContent(), null, "https://sms7.schoolsoft.se/nti/jsp/student/right_student_schedule.jsp");
            	
            	//Needs to be validated, so users don't supply regex and f up everything
            	Elements studentContainers = classDoc.getElementsByClass("heading_bold");
            	Elements students = new Elements();
            	for(Element s : studentContainers){
            		if(s.getElementById("name") != null && s.getElementById("name").html().toString().contains(name)){
            			students.add(s.getElementById("name"));
            		}
            	}
            	if(students.size() > 0){
            		for(int j=0;j<students.size();j++){
            			names.add(replaceHTML(students.get(j).html().toString()));
            			classes.add(replaceHTML(classesItems.get(i).getElementsByTag("a").get(0).html()));
            		}
            	}
            }
            if(names.size() > 0){
            	for(int i=0;i<names.size();i++){
            		Elements listItems = parent.getElementsContainingOwnText(classes.get(i));
            		for(int j=0;j<listItems.size();j++){
        				Element listItem = listItems.get(j);
        				String url = listItem.getElementsByTag("a").get(0).attr("href").toString();
        				String requestId = url.split("requestid\\=")[1];
        				scheduleUrls.add(requestId);
        				//scheduleUrls.add(url);
            			/*if(!(listItem.html().contains("Klasslista")) && !(url.equals("#"))){
            				
            				HttpPost httpPost5 = new HttpPost("https://sms7.schoolsoft.se/nti/jsp/student/right_student_schedule.jsp?term=6&requestid=" + requestId);
            				HttpResponse response5 = httpclient.execute(httpPost5);
            				Document scheduleDoc = Jsoup.parse(response5.getEntity().getContent(), null, "https://sms7.schoolsoft.se/nti/jsp/student/right_student_schedule.jsp");
            				Schedule schedule = parseScheduleDoc(scheduleDoc);
            				schedules.add(schedule);
            			}*/
            		}
            	}
            }
            else
            	Log.d(TAG, "No matches found for name: " + name);   
		}
		catch(IOException e){
			Log.e(TAG, "IOException thrown while getting schedule for name: " + name);
			Log.e(TAG, e.getMessage());
			check = 3;
		}
		catch(IllegalArgumentException e){
			Log.e(TAG, "IllegalArgumentException when getting schedule for name: " + name);
			Log.e(TAG, e.getMessage());
			check = 2;
		}
		result.setNames(names);
		result.setClasses(classes);
		result.setSchedules(scheduleUrls);
		return result;
	}
	
	public List<Person> getAllStudents(){
		Log.d(T, "GETTING STUDENTS");
		List<Person> students = new ArrayList<Person>();
		
		try{
    		login();
            HttpPost httppost2 = new HttpPost("https://sms7.schoolsoft.se/nti/jsp/student/right_student_class.jsp");
            HttpResponse response2 = httpclient.execute(httppost2);
            Document sceDoc = Jsoup.parse(response2.getEntity().getContent(),null,"https://sms7.schoolsoft.se/nti/jsp/student/right_student_class.jsp");
            
            Element parent = sceDoc.getElementsByClass("dropdown-menu").first();
            Elements classesItems = parent.getElementsByTag("li");
            //once for every class
            for(int i=0;i<classesItems.size();i++){
            	Element element = classesItems.get(i);
            	String className = element.getElementsByTag("a").get(0).html();
            	String subUrl = element.getElementsByTag("a").get(0).attr("href").toString();
            	HttpPost httpPost4 = new HttpPost("https://sms7.schoolsoft.se/nti/jsp/student/" + subUrl);
            	HttpResponse response4 = httpclient.execute(httpPost4);
            	Document classDoc = Jsoup.parse(response4.getEntity().getContent(), null, "https://sms7.schoolsoft.se/nti/jsp/student/right_student_schedule.jsp");
            	
            	//Needs to be validated, so users don't supply regex and f up everything
            	Elements studentContainers = classDoc.getElementById("contAll_content").getElementsByClass("table").first().getElementsByClass("heading_bold");
            	Elements studentElements = new Elements();
            	//once for every student
            	for(Element s : studentContainers){
            		if(s.getElementById("name") != null){
            			studentElements.add(s.getElementById("name"));
            		}
            	}
            	if(studentElements.size() > 0){
            		for(int j=0;j<studentElements.size();j++){
            			Person student = new Person();
            			student.setName(replaceHTML(studentElements.get(j).html().toString()));
            			student.setClassName(replaceHTML(classesItems.get(i).getElementsByTag("a").get(0).html().toString()));
            			student.setScheduleId(subUrl.split("requestid\\=")[1]);
            			students.add(student);
            		}
            	}
            }   
		}
		catch(IOException e){
			Log.e(TAG, "IOException thrown while getting all schedules");
			Log.e(TAG, e.getMessage());
			check = 3;
		}
		catch(IllegalArgumentException e){
			Log.e(TAG, "IllegalArgumentException when getting all schedules");
			Log.e(TAG, e.getMessage());
			check = 2;
		}
		return students;
	}
	
	public List<Person> getAllTeachers(){
		Log.d(T, "GETTING TEACHERS");
		List<Person> teachers = new ArrayList<Person>();
		try{
    		login();
            HttpPost httppost2 = new HttpPost("https://sms7.schoolsoft.se/nti/jsp/student/right_student_schedule.jsp?&type=1&teacher=1729");
            HttpResponse response2 = httpclient.execute(httppost2);
            Document sceDoc = Jsoup.parse(response2.getEntity().getContent(),null,"https://sms7.schoolsoft.se/nti/jsp/student/right_student_class.jsp");
            
            Elements listElement = sceDoc.getElementsByClass("dropdown-menu");
            Elements teachersItems = listElement.get(0).getElementsByTag("a");
            //once for every class
            for(int i=0;i<teachersItems.size();i++){
            	Element element = teachersItems.get(i);
            	//The first item in the list says "välj en lärare", we don't want that to show up
            	if(!(element.html().toString().contains("---"))){
	            	Person teacher = new Person();
	            	teacher.setName(replaceHTML(element.html().toString()));
	            	teacher.setClassName("Lärare");
	            	String subUrl = element.attr("href").toString();
	            	teacher.setScheduleId(subUrl.split("teacher\\=")[1]);
	            	teachers.add(teacher);
            	}
            }   
		}
		catch(IOException e){
			Log.e(TAG, "IOException thrown while getting all teachers");
			Log.e(TAG, e.getMessage());
			check = 3;
		}
		catch(IllegalArgumentException e){
			Log.e(TAG, "IllegalArgumentException when getting all teachers");
			Log.e(TAG, e.getMessage());
			check = 2;
		}
		return teachers;
	}
	
	public Schedule getScheduleForPerson(Person person, LoadScheduleTask task, int personType) throws ScheduleRetreivalFailedException{
		Schedule schedule = new Schedule();
		
		try{
    		login();
    		if(personType == PERSON_TYPE_STUDENT){
                HttpPost httppost2 = new HttpPost(SCHEDULE_BASE_URL + "?requestid=" + person.getScheduleId());
                HttpResponse response2 = httpclient.execute(httppost2);
                Document sceDoc = Jsoup.parse(response2.getEntity().getContent(),null,"https://sms7.schoolsoft.se/nti/jsp/student/right_student_class.jsp");
                schedule = parseScheduleDoc(sceDoc, task);
    		}
    		else if(personType == PERSON_TYPE_TEACHER){
                HttpPost httppost2 = new HttpPost(SCHEDULE_BASE_URL + "?teacher=" + person.getScheduleId());
                HttpResponse response2 = httpclient.execute(httppost2);
                Document sceDoc = Jsoup.parse(response2.getEntity().getContent(),null,"https://sms7.schoolsoft.se/nti/jsp/student/right_student_class.jsp");
                schedule = parseScheduleDoc(sceDoc, task);	
    		}   
            return schedule;      
		}
		catch(IOException e){
			Log.e(TAG, "IOException thrown while getting schedule for name: " + person.getName());
			Log.e(TAG, e.getMessage());
			check = 3;
		}
		catch(IllegalArgumentException e){
			Log.e(TAG, "IllegalArgumentException when getting schedule for name: " + person.getName());
			Log.e(TAG, e.getMessage());
			check = 2;
		}
		throw new ScheduleRetreivalFailedException("Schedule retreival for person: " + person.getName() + " failed.");
	}
	
	//DEPRECATED!!!
	public List<String> getNewsCategories(){
		Log.d(TAG, "GETTING NEWS CATEGORIES");
		List<String> categories = new ArrayList<String>();
		
		try{
			login();
			HttpPost post = new HttpPost(URL_GET_CATEGORIES);
			HttpResponse response = httpclient.execute(post);
			Document newsDoc = Jsoup.parse(response.getEntity().getContent(), null, URL_BASE_NEWS);
			
			//Since this page in particular only has one element with the selectors class we identify the element we need by that.
			Element selector = newsDoc.getElementsByClass("selectors").get(0);
			Elements categoriesElements = selector.getElementsByTag("li");
			for(Element c : categoriesElements){
				Element category = c.getElementsByTag("a").get(0);
				categories.add(replaceHTML(category.html().toString()));
			}
			
			return categories;
		}
		catch(Exception e){
			Log.e(TAG, e.getMessage());
			return null;
		}
	}
	public List<News> getNews(){
		Log.d(TAG, "GETTING NEWS");
		List<News> newsList = new ArrayList<News>();
		try{
			login();
			HttpPost post = new HttpPost(URL_BASE_NEWS);
			HttpResponse response = httpclient.execute(post);
			Document newsDoc = Jsoup.parse(response.getEntity().getContent(), null, URL_BASE_NEWS);
			Elements newsContainer = newsDoc.getElementById("news_con_content").children();
			
			String currentCategory = "";
			for(int index=0;index<(newsContainer.size()+1)/2;index++){
				Element newsCategoryContainer = newsContainer.get(index*2);
				Elements accGroups = newsCategoryContainer.getElementsByClass("accordion-group");
				for(Element element : accGroups){
					News news = new News();
					
					//Get id
					int id;
					try{
						//Okay tbh I have no idea why it's "acc-item" and not "accordion-group" but don't mind that.
						id = Integer.parseInt(element.attr("id").toString().split("acc\\-item\\-")[1]);
						news.setId(id);
					}
					catch(NumberFormatException e){
						Log.e(TAG, "Could not parse schoolsoft news id to int");
						continue;
					}
					
					//Get title
					Element mElement = element.getElementsByClass("accordion-heading-left").get(0);
					String title = replaceHTML(mElement.getElementsByTag("span").get(0).html().toString());
					news.setTitle(title);
					
					//Get content		
					if(!(element.getElementsByClass("acc-item-main").isEmpty()) &&
							!(element.getElementsByClass("acc-item-main").get(0).children().isEmpty())){
						mElement = element.getElementsByClass("acc-item-main").get(0).child(0);
						//Yes, it's an if shorthand, deal with it >:/
						String content = (!(mElement.html().toString().isEmpty())) ? replaceHTML(mElement.html().toString()) : "Ingen beskrivning";
						news.setContent(content);
					}
					else{
						news.setContent("Ingen beskrivning.");
					}
					
					//Get date published
					Date published = null;
					mElement = element.getElementsByClass("accordion-heading-date-wide").get(0);
					String date = replaceHTML(mElement.html().toString());
					if(date.equalsIgnoreCase("idag")){
						published = new Date();
					}
					else if(date.equalsIgnoreCase("igår")){
						published = new Date(new Date().getTime() - 1000 * 60 * 60* 24);
					}
					else{
						if(date.contains("okt")){
							date = date.replace("okt", "oct");
						}
						if(date.contains("maj")){
							date = date.replace("maj", "may");
						}
						String[] formats = {"dd MMM", "dd MMM yyyy"};
						for(String format : formats){
							try{
								SimpleDateFormat sdf = new SimpleDateFormat(format);
								published = sdf.parse(date);
							}
							catch(ParseException e){}
						}
					}
					if(published != null){
						news.setPublished(published);
					}
					else{
						Log.e(TAG, "News date parsing failed");
					}
					
					//Get author/published by
					if(!(element.getElementsByClass("inner_right_info").isEmpty()) &&
							!(element.getElementsByClass("inner_right_info").get(0).children().isEmpty())){
						mElement = element.getElementsContainingOwnText("(P)").first();
						//Split author string on on "(P)" since all authors contain this, for unknown reasons . __.)
						String author = replaceHTML(mElement.html().toString().split("\\(P\\)")[0]);
						news.setAuthor(author);
					}
					else{
						news.setAuthor("Ingen författare");
					}
					
					newsList.add(news);
				}
				if(index*2 != newsContainer.size()-1){
					currentCategory = replaceHTML(newsContainer.get((index*2)+1).html().toString());
				}
			}
			
			return newsList;
		}
		catch(Exception e){
			Log.e(TAG, "Exception caught while getting news");
			return null;
		}
	}
	
	public int getNumSchedules(){
		login();
		int numSchedules = 0;
		
		try{
			HttpPost httppost2 = new HttpPost("https://sms7.schoolsoft.se/nti/jsp/student/right_student_class.jsp");
	        HttpResponse response2 = httpclient.execute(httppost2);
	        Document sceDoc = Jsoup.parse(response2.getEntity().getContent(),null,"https://sms7.schoolsoft.se/nti/jsp/student/right_student_class.jsp");
	        
	        Element parent = sceDoc.getElementsByClass("dropdown-menu").first();
	        Elements classesItems = parent.getElementsByTag("li");
	        
	        numSchedules += classesItems.size();
	        
			HttpPost post = new HttpPost("https://sms7.schoolsoft.se/nti/jsp/student/right_student_schedule.jsp?type=1");
        	response2 = httpclient.execute(post);
			sceDoc = Jsoup.parse(response2.getEntity().getContent(),null,"https://sms7.schoolsoft.se/nti/jsp/student/right_student_class.jsp");
			
			parent = sceDoc.getElementsByClass("dropdown-menu").first();
	        classesItems = parent.getElementsByTag("li");
	        
	        numSchedules += classesItems.size();
	        
		}
		catch(Exception e){}
		return numSchedules;
	}
	
	public List<Schedule> getAllSchedules(Runnable onScheduleLoaded){
		List<Schedule> schedules = new ArrayList<Schedule>();
		
		login();
		
		try{
			HttpPost httppost2 = new HttpPost("https://sms7.schoolsoft.se/nti/jsp/student/right_student_class.jsp");
	        HttpResponse response2 = httpclient.execute(httppost2);
	        Document sceDoc = Jsoup.parse(response2.getEntity().getContent(),null,"https://sms7.schoolsoft.se/nti/jsp/student/right_student_class.jsp");
	        
	        Element parent = sceDoc.getElementsByClass("dropdown-menu").first();
	        Elements classesItems = parent.getElementsByTag("li");
	        //once for every class
	        Log.d(TAG, "classes: " + classesItems.size());
	        for(int i=0;i<classesItems.size();i++){
	        	Log.d(TAG, "loop schedule");
	        	Element element = classesItems.get(i);
	        	String className = element.getElementsByTag("a").get(0).html();
	        	String subUrl = element.getElementsByTag("a").get(0).attr("href").toString();
	        	subUrl = subUrl.replace("class", "schedule");
	        	HttpPost httpPost4 = new HttpPost("https://sms7.schoolsoft.se/nti/jsp/student/" + subUrl);
	        	HttpResponse response4 = httpclient.execute(httpPost4);
	        	Document scheduleDoc = Jsoup.parse(response4.getEntity().getContent(), null, "https://sms7.schoolsoft.se/nti/jsp/student/right_student_schedule.jsp");
	        	
	        	Schedule s = parseScheduleDoc(scheduleDoc, null);
	        	Log.d(TAG, "" + subUrl.split("requestid\\=")[1]);
	        	int id = Integer.parseInt(subUrl.split("requestid\\=")[1]);
	        	s.setClassName(className);
	        	s.setId(id);
	        	schedules.add(s);
	        	onScheduleLoaded.run();
	        	
	        	Log.d(TAG, "schedule added");
	        }
	        
	        List<Schedule> teacherSchedules = getTeacherSchedules(onScheduleLoaded);
	        schedules.addAll(teacherSchedules);
			
			return schedules;
		}
		catch(Exception e){
			Log.e(TAG, e.getMessage());
		}
		return null;
	}
//------------------------------------------------------------------------------------------------------------------------------------
//------------------------------------------------------------PRIVATE METHODS---------------------------------------------------------
//------------------------------------------------------------------------------------------------------------------------------------
	private List<Schedule> getTeacherSchedules(Runnable onScheduleLoaded){
		List<Schedule> schedules = new ArrayList<Schedule>();
		HttpPost post = new HttpPost("https://sms7.schoolsoft.se/nti/jsp/student/right_student_schedule.jsp?type=1");
        try {
        	HttpResponse response2 = httpclient.execute(post);
			Document sceDoc = Jsoup.parse(response2.getEntity().getContent(),null,"https://sms7.schoolsoft.se/nti/jsp/student/right_student_class.jsp");
			
			Element parent = sceDoc.getElementsByClass("dropdown-menu").first();
	        Elements classesItems = parent.getElementsByTag("li");
	        
	        for(int i=0;i<classesItems.size();i++){
	        	Element element = classesItems.get(i);
	        	String className = "Lärare";
	        	if(!element.text().contains("(HK)")){
	        		String subUrl = element.getElementsByTag("a").get(0).attr("href").toString();
	        		int id = Integer.parseInt(subUrl.split("teacher\\=")[1]);
		        	HttpPost httpPost4 = new HttpPost("https://sms7.schoolsoft.se/nti/jsp/student/" + subUrl);
		        	HttpResponse response4 = httpclient.execute(httpPost4);
		        	Document scheduleDoc = Jsoup.parse(response4.getEntity().getContent(), null, "https://sms7.schoolsoft.se/nti/jsp/student/right_student_schedule.jsp");
		        	
		        	Schedule s = parseScheduleDoc(scheduleDoc, null);
		        	s.setClassName(className);
		        	s.setId(id);
		        	schedules.add(s);
		        	onScheduleLoaded.run();
	        	}
	        }
		} 
        catch (IllegalStateException e) {
        	Log.e(TAG, e.getMessage());
        } 
        catch (IOException e) {
        	Log.e(TAG, e.getMessage());
        }
        
        return schedules;
	}
	private Schedule parseScheduleDoc(Document doc, LoadScheduleTask task){
		Schedule schedule = new Schedule();
    	try{		
            
			//Find all elements with "schedule" class, i.e every period element in the displayed schedule
            Elements sceEle = doc.getElementsByClass("schedule");
            Vector<String>temps = new Vector<String>();
			//Loops through all periods in the schedule
            for(int i=0;i<sceEle.size();i++){
				//Fetches the ajax url for every period
            	String scheduleUrl = sceEle.get(i).attr("href").toString();
            	String ajax = scheduleUrl.substring(scheduleUrl.indexOf('?') + 1, scheduleUrl.length() - 14);
            	temps.add(ajax);
            }
            
            //Loops through all the "pages" for the periods, i.e the one you get redirected to with the ajax url
            for(int j = 0; j < temps.size(); j++){
            	
            	Period period = new Period(context);
            	
		    	HttpPost httppost3 = new HttpPost("https://sms7.schoolsoft.se/nti/jsp/student/right_student_schedule_ajax.jsp?" + temps.elementAt(j));
	            HttpResponse response3 = httpclient.execute(httppost3);
			    
			    Document mDoc = Jsoup.parse(response3.getEntity().getContent(), "ISO-8859-1","https://sms7.schoolsoft.se/nti/jsp/student/right_student_schedule_ajax.jsp?" + temps.elementAt(j));
			    
			    period = parsePeriodDoc(mDoc);
			    schedule.addPeriodToDay(period, period.getDay());
		    }
        }
    	catch (IOException e){
    		check = 3;
    	}
    	catch (IllegalArgumentException e){
    		check = 2;
    	}
    	return schedule;
	}
    
    //Parses the ajax page of a period 
    private Period parsePeriodDoc(Document doc){
    	Period period = new Period(context);
    	
    	Element e = doc.getElementById("hiddenheader");
		if(e == null || e.text() == null || e.text().indexOf(":") == -1){
			//Break out of loop, go to next period
			return null;
		}
		String subject = e.text().substring(0, findDay(e.text()));
		
		period.setSubject(subject);
		
		String time = "";
		//Find the start and end date of the period
		//REGEX: any one or two digits, one colon, any two digits, one line, any one or two digits, one colon, any two digits
		//REGEX EX: "8:20-9:40" "16:54-00:00"
		Pattern p = Pattern.compile("\\d{1,2}:{1}\\d{2}-{1}\\d{1,2}:{1}\\d{2}");
		Matcher m = p.matcher(e.text());
		if(m.find()){
			time = m.group(0);
		}
		else{
			return null;
		}
		String timeStart = "";
		String timeEnd = "";
		if(time.split("-").length < 2){
			return null;
		}
		else{
			timeStart = time.split("-")[0];
			timeEnd = time.split("-")[1];
		}
		timeStart = trimTime(timeStart);
		timeEnd = trimTime(timeEnd);  	
		
		Time mTimeStart = parseTime(timeStart);
		Time mTimeEnd = parseTime(timeEnd);
		
		period.setStartTime(mTimeStart);
		period.setEndTime(mTimeEnd);
		
		if(timeStart.length() < 5){
			timeStart = "0" + timeStart;
		}
		if(timeEnd.length() < 5){
			timeEnd = "0" + timeEnd;
		}
		int dayInt = convertDay(getDay(e.text()));
		period.setDay(dayInt);
		
		int numComma = e.text().replaceAll("[^,]", "").length();
		
		String prePlace[] = e.text().split(",");
		String place = "";
		if(prePlace.length < 2){
			place = "Ingen sal";
		}
		else{
			place = prePlace[numComma];
    		place = place.trim();
		}
		period.setRoom(place);
		
		return period;
    }
    private int checkIfSuccess(InputStream is){
    	int login = 0;
    	try{
    		// Wrap a BufferedReader around the InputStream
            BufferedReader rd = new BufferedReader(new InputStreamReader(is), 8 * 1024);

            // Read response until the end
            String line;
        	while ((line = rd.readLine()) != null) {
        		//System.out.println(line);
        		if(line.contains("Felmeddelande")){
        			 System.out.println("HELLO");
        			login = 2;
        			break;
        		}
        		else if(line.contains("Varning") || line.contains("Warning")){
        			System.out.println("HELLO2");
        			login = 1;
        			break;
        		}
        		else{
        			login = 0;
        		}
    	    }
        	
    	}
    	catch (IOException e) {
			System.out.println(e);
		}
    	
    	return login;
    }
    private String trimTime(String text){
    	String trimmed = text;
    	trimmed = trimmed.replaceAll(",", "");
    	trimmed = trimmed.replaceAll(" ", "");
    	return trimmed;
    }
    private int findDay(String st){
    	String days[] = {"måndag", "tisdag","onsdag","torsdag","fredag","Måndag","Tisdag","Onsdag","Torsdag","Fredag"};
    	int p = 5;
    	for(int i = 0; i < days.length; i++){
    		if(st.indexOf(days[i]) != -1){
    			p = st.indexOf(days[i]);
    			return p;
    		}
    	}
    	
    	Log.e(TAG, "Couldn't find index of any day in text " + st);
    	return -1;
    }
    private String getDay(String text){
    	String days[] = {"måndag", "tisdag","onsdag","torsdag","fredag"};
    	String st = text.toLowerCase(new Locale("sv", "SE"));
    	for(int i=0;i<days.length;i++){
    		if(st.indexOf(days[i]) != -1){
    			return days[i];
    		}
    	}
		Log.e(TAG, "Couldn't find any day in text: " + text);
		return null;
    }
    public String replaceHTML(String text){
    	String cleanText = Html.fromHtml(text).toString();
    	
    	/*cleanText = cleanText.replace("&aring;", "å");
    	cleanText = cleanText.replace("&Aring;", "Å");
    	cleanText = cleanText.replace("&auml;", "ä");
    	cleanText = cleanText.replace("&Auml;", "Ä");
    	cleanText = cleanText.replace("&ouml;", "ö");
    	cleanText = cleanText.replace("&Ouml;", "Ö");
    	cleanText = cleanText.replace("<br />", " "); */
    	
    	return cleanText;
    }
    private int convertDay(String theDay){
    	String day = theDay;
    	int dayInt = 0;
    	if(day.equalsIgnoreCase("måndag")){dayInt = 0;}
    	else if(day.equalsIgnoreCase("tisdag")){dayInt = 1;}
    	else if(day.equalsIgnoreCase("onsdag")){dayInt = 2;}
    	else if(day.equalsIgnoreCase("torsdag")){dayInt= 3;}
    	else if(day.equalsIgnoreCase("fredag")){dayInt = 4;}
    	else if(day.equalsIgnoreCase("lördag")){dayInt = 5;}
    	else if(day.equalsIgnoreCase("söndag")){dayInt = 6;}
    	return dayInt;
    }
	
	public URI getLocationURI(HttpResponse response, HttpContext context)
			throws ProtocolException {
		return null;
	}
	
	public Time parseTime(String timeString){
		Time time;
		DateFormat formatter = new SimpleDateFormat("HH:mm", new Locale("sv", "SE"));
		try{
			time = new Time(formatter.parse(timeString).getTime());
		}
		catch(ParseException e){
			Log.e(TAG, "Error parsing time: " + timeString);
			return new Time(0);
		}
		return time;
	}
	
	public boolean isRedirectRequested(HttpResponse response,
			HttpContext context) {
		return false;
	}
}