package com.nti.flux.ntiapp.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.nti.flux.ntiapp.R;
import com.nti.flux.ntiapp.activity.SearchSchedulesActivity;
import com.nti.flux.ntiapp.adapter.SearchPagerAdapter;
import com.nti.flux.ntiapp.dbhelper.SearchDbHelper;
import com.nti.flux.ntiapp.exception.InvalidSchoolsoftAuthException;
import com.nti.flux.ntiapp.exception.NoScheduleFoundException;
import com.nti.flux.ntiapp.exception.ScheduleRetreivalFailedException;
import com.nti.flux.ntiapp.helper.ImportSchoolsoft;
import com.nti.flux.ntiapp.helper.ScheduleSearcher;
import com.nti.flux.ntiapp.model.OnAsyncTaskCompleted;
import com.nti.flux.ntiapp.model.Person;
import com.nti.flux.ntiapp.model.Schedule;
import com.nti.flux.ntiapp.service.NtiService;

public class SearchSchedulesFragment extends Fragment implements OnAsyncTaskCompleted{
	private static final String TAG = SearchSchedulesFragment.class.getName();
	
	private static final int SEARCH_STUDENTS = 0, SEARCH_TEACHERS = 1, SEARCH_ROOMS = 2;
	
	EditText searchBar;
	private ListView results;
	private SearchPagerAdapter mPagerAdapter;
	int searchType = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state){
		View rootView = inflater.inflate(R.layout.fragment_search, container, false);
		results = (ListView)rootView.findViewById(R.id.search_results);	
		
		searchBar = (EditText)rootView.findViewById(R.id.name_search);
		searchBar.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				boolean handled = false;
				if(actionId == EditorInfo.IME_ACTION_SEARCH){
					search();
				}
				return handled;
			}
		});
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.search_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner s = (Spinner)rootView.findViewById(R.id.spinner_search_type);
		s.setAdapter(adapter);
		s.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				searchType = (position == 0) ? 0 : 1;
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
		});
		/*ImageButton searchButton = (ImageButton)rootView.findViewById(R.id.search_button);
		searchButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				search();
			}
		});*/
		
		//If the service is updating the database things get a bit iffy, so we'll show a progressdialog here until it's done
		if(NtiService.updatingStudents){
			new WaitForDb(getActivity()).execute();
		}
		
		return rootView;
	}
	public void search(View v){search();};
	public void search(){
		new SearchTask(this.getActivity(), this, searchType).execute(searchBar.getText().toString());
	}
	
	public static SearchSchedulesFragment newInstance(SearchPagerAdapter adapter){
		SearchSchedulesFragment fragment = new SearchSchedulesFragment();
		fragment.setAdapter(adapter);
		return fragment;
	}
	
	public class SearchTask extends AsyncTask<String, String, Boolean>{
		
		Context context;
		ProgressDialog pd;
		List<Person> people = new ArrayList<Person>();
		private OnAsyncTaskCompleted listener;
		int searchType;
		
		public SearchTask(Context context, OnAsyncTaskCompleted listener, int type){
			this.context = context;
			pd = new ProgressDialog(context);
			this.listener = listener;
			searchType = type;
		}
		protected void onPreExecute(){
			pd.setTitle("Söker...");
			pd.setMessage("Söker efter namn...");
			pd.setCancelable(false);
			pd.show();
		}
		protected void onPostExecute(Boolean result){
			if(pd.isShowing()){
				pd.dismiss();
			}
			listener.onTaskCompleted(people);
		}
		
		@Override
		protected Boolean doInBackground(String... name) {
			SearchDbHelper dbHelper = new SearchDbHelper(context);
			switch(searchType){
			case SEARCH_STUDENTS:
				people = dbHelper.getStudentsByName(name[0]);
				break;
			case SEARCH_TEACHERS:
				people = dbHelper.getTeachersByName(name[0]);
				break;
			case SEARCH_ROOMS:
				//get rooms
				break;
			}
			return true;
		}
	}
	
	@Override
	public void onTaskCompleted(final List<Person> people){
		ArrayList<String> titles = new ArrayList<String>();
		for(Person person : people){
			String title = "";
			if(searchType == SEARCH_STUDENTS)
				title = person.getName() + ", " + person.getClassName();
			else if(searchType == SEARCH_TEACHERS)
				title = person.getName();
			titles.add(title);
		}
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
				getActivity(),
				R.layout.listitem_search_result,
				R.id.list_content,
				titles);
		results.setAdapter(arrayAdapter);
		results.setClickable(true);
		final Context c = getActivity();
		results.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				ProgressDialog dialog = new ProgressDialog(c);
				dialog.setTitle("Laddar schema");
				dialog.setMessage("Ett ögonblick...");
				dialog.setIndeterminate(true);
				dialog.show();
				new LoadScheduleTask(c, people.get(pos), searchType, dialog).execute();
			}
		});
	}
	
	public class LoadScheduleTask extends AsyncTask<Void, Void, Boolean>{
		Context context;
		Person searchPerson;
		Schedule schedule;
		int searchType;
		ProgressDialog dialog;
		
		LoadScheduleTask(Context contex, Person searchPerson, int searchType, ProgressDialog pd){
			this.context = contex;
			this.searchPerson = searchPerson;
			this.searchType = searchType;
			dialog = pd;
		}
		protected void onPostExecute(Boolean result){
			mPagerAdapter.setSchedule(schedule);
			SearchSchedulesActivity parent = (SearchSchedulesActivity)getActivity();
			parent.getPager().setCurrentItem(1);
			if(dialog != null && dialog.isShowing()){
				dialog.dismiss();
			}
		}
		
		@Override
		protected Boolean doInBackground(Void... arg0){
			try{
				ImportSchoolsoft is = new ImportSchoolsoft(context);
				schedule = is.getScheduleForPerson(searchPerson, this, searchType);
				return true;
			}
			catch(ScheduleRetreivalFailedException e){
				Log.e(TAG, e.getMessage());
			}
			catch(InvalidSchoolsoftAuthException e){
				Log.e(TAG, e.getMessage());
			}
			return false;
		}
	}
	
	private class WaitForDb extends AsyncTask<Void, Void, Boolean>{
		
		private ProgressDialog pd;
		
		public WaitForDb(Context context){
			pd = new ProgressDialog(context);
		}
		
		protected void onPreExecute(){
			pd.setTitle("Ett ögonblick...");
			pd.setMessage("Uppdaterar lista på elever. Oroa dig inte, det händer inte varje gång.");
			pd.setCancelable(false);
			pd.show();
		}
		protected void onPostExecute(Boolean result){
			pd.dismiss();
		}
		@Override
		protected Boolean doInBackground(Void... arg0) {
			while(NtiService.updatingStudents){}
			return true;
		}
		
	}
	
	public void setAdapter(SearchPagerAdapter adapter){
		mPagerAdapter = adapter;
	}
}
