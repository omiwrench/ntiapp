package com.nti.flux.ntiapp.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.content.Context;
import android.util.Log;

import com.nti.flux.ntiapp.R;
import com.nti.flux.ntiapp.model.Restaurants;

public class RestaurantsHelper {
	private static final String TAG = RestaurantsHelper.class.getName();

	private Context context;
	
	public RestaurantsHelper(Context c){
		context = c;
	}
	
	public Restaurants getRestaurants(){
		String url = context.getString(R.string.url_restaurants_file);
		String xml = "";
		
		HttpResponse response = null;
		try{
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();
			request.setURI(new URI(url));
			response = client.execute(request);
		}
		catch(Exception e){
			Log.e(TAG, "Exception thrown when getting restaurants file");
			return null;
		}
		if(response != null){
			try {
				xml = getStringFromInputStream(response.getEntity().getContent());
			} catch (IllegalStateException e) {
				Log.e(TAG, e.getMessage());
				return null;
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
				return null;
			}
		}
		if(xml != null){
			Log.d(TAG, xml);
			try{
				Serializer serializer = new Persister();
				StringReader reader = new StringReader(xml);
				Restaurants restaurants = serializer.read(Restaurants.class, reader);
				return restaurants;
			}
			catch(Exception e){
				Log.e(TAG, e.getMessage());
				return null;
			}
		}
		return null;
	}
	
	// convert InputStream to String
		private static String getStringFromInputStream(InputStream is) {
	 
			BufferedReader br = null;
			StringBuilder sb = new StringBuilder();
	 
			String line;
			try {
	 
				br = new BufferedReader(new InputStreamReader(is));
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
	 
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						Log.e(TAG, e.getMessage());
					}
				}
			}
	 
			return sb.toString();
	 
		}
}
