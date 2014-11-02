package com.nti.flux.ntiapp.fragment;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nti.flux.ntiapp.R;
import com.nti.flux.ntiapp.SplashActivity;
import com.nti.flux.ntiapp.helper.DatabaseUpdater;
import com.nti.flux.ntiapp.helper.ImportSchoolsoft;

public class FirstTimeFragment extends Fragment{
	private static final String TAG = FirstTimeFragment.class.getName();
	
	ViewGroup main, loginFailed, syncingApp;
	EditText usernameView, passwordView;
	SplashActivity parent;

	public static FirstTimeFragment newInstance(SplashActivity a){
		FirstTimeFragment f = new FirstTimeFragment();
		f.parent = a;
		return f;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View v = inflater.inflate(R.layout.fragment_first_time, container, false);
		usernameView = (EditText) v.findViewById(R.id.first_time_username);
		passwordView = (EditText) v.findViewById(R.id.first_time_password);
		
		Typeface titleFont = Typeface.createFromAsset(getActivity().getAssets(), getString(R.string.font_main_menu_button));
		TextView title = (TextView)v.findViewById(R.id.first_time_hello_text);
		title.setTypeface(titleFont);
		
		Typeface contentType = Typeface.createFromAsset(getActivity().getAssets(), getResources().getString(R.string.font_content));
		TextView content = (TextView)v.findViewById(R.id.first_time_description);
		content.setTypeface(contentType);
		
		Button goButton = (Button)v.findViewById(R.id.first_time_go_button);
		goButton.setTypeface(contentType);
		goButton.setOnClickListener(goButtonClicked);
		
		TextView syncingTitle = (TextView)v.findViewById(R.id.text_syncing_title);
		syncingTitle.setTypeface(titleFont);
		
		TextView syncingDescription = (TextView)v.findViewById(R.id.text_syncing_description);
		syncingDescription.setTypeface(contentType);
		
		TextView loginFailedText = (TextView)v.findViewById(R.id.text_login_failed);
		loginFailedText.setTypeface(titleFont);
		
		TextView loginFailedDescription = (TextView)v.findViewById(R.id.text_login_failed_description);
		loginFailedDescription.setTypeface(contentType);
		
		Button tryAgainButton = (Button)v.findViewById(R.id.button_try_again);
		tryAgainButton.setTypeface(contentType);
		tryAgainButton.setOnClickListener(onTryAgainClicked);
		
		Button continueButton = (Button)v.findViewById(R.id.button_continue_anyway);
		continueButton.setTypeface(contentType);
		
		main = (ViewGroup)v.findViewById(R.id.container_login);
		loginFailed = (ViewGroup)v.findViewById(R.id.container_login_failed);
		syncingApp = (ViewGroup)v.findViewById(R.id.container_syncing_schoolsoft);
		loginFailed.setVisibility(View.GONE);
		syncingApp.setVisibility(View.GONE);
		
		return v;
	}
	
	private void showLoginFailed(){
		main.setVisibility(View.GONE);
		syncingApp.setVisibility(View.GONE);
		loginFailed.setVisibility(View.VISIBLE);
	}
	
	private void populateDatabase(){
		main.setVisibility(View.GONE);
		loginFailed.setVisibility(View.GONE);
		syncingApp.setVisibility(View.VISIBLE);
		
		ProgressBar pb = new ProgressBar(this.getActivity(), null, android.R.attr.progressBarStyleHorizontal);
		pb.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		syncingApp.addView(pb);
		DatabaseUpdater du = new DatabaseUpdater(this.getActivity());
		du.updateDatabaseWithProgress(syncingFinished, pb);
	}
	
	OnClickListener onTryAgainClicked = new OnClickListener(){
		@Override
		public void onClick(View arg0) {
			main.setVisibility(View.VISIBLE);
			syncingApp.setVisibility(View.GONE);
			loginFailed.setVisibility(View.GONE);
			
			usernameView.setText("");
			passwordView.setText("");
		}
	};
	
	Runnable syncingFinished = new Runnable(){
		@Override
		public void run(){
			parent.exitLoginQuery();
		}
	};
	
	private OnClickListener goButtonClicked = new OnClickListener(){

		@Override
		public void onClick(View v) {
			if(usernameView.getText().length() > 0 && passwordView.getText().length() > 0){
				final String username = usernameView.getText().toString();
				final String password = passwordView.getText().toString();
				new VerifyCredentialsTask(username, password).execute();
			}
		}
	};
	
	private class VerifyCredentialsTask extends AsyncTask<Void, Void, Boolean>{

		private ImportSchoolsoft iS;
		private String user, pass;
		
		public VerifyCredentialsTask(String username, String password){
			iS = new ImportSchoolsoft(FirstTimeFragment.this.getActivity(), username, password, 1);
			user = username;
			pass = password;
		}
		
		@Override
		protected Boolean doInBackground(Void... arg0) {
			return iS.confirmLogin();
		}
		@Override
		protected void onPostExecute(Boolean result){
			if(result){
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(FirstTimeFragment.this.getActivity());
				prefs.edit().putString("pref_schoolsoft_username", user).putString("pref_schoolsoft_password", pass).apply();
				populateDatabase();
			}
			else{
				showLoginFailed();
			}
		}
	}
}
