package com.nti.flux.ntiapp.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Window;
import android.widget.ArrayAdapter;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nti.flux.ntiapp.R;
import com.nti.flux.ntiapp.adapter.RestaurantsPanelAdapter;
import com.nti.flux.ntiapp.dbhelper.RestaurantsDbHelper;
import com.nti.flux.ntiapp.listener.OnRestaurantSelectedListener;
import com.nti.flux.ntiapp.model.Restaurant;
import com.nti.flux.ntiapp.model.Restaurants;
import com.nti.flux.ntiapp.view.NonSwipeableViewPager;

public class RestaurantsActivity extends FragmentActivity implements OnMarkerClickListener, OnRestaurantSelectedListener{
	private static final String TAG = RestaurantsActivity.class.getName();
	
	private GoogleMap map;
	private List<Marker> restaurantMarkers = new ArrayList<Marker>();
	ArrayAdapter<Restaurant> namesAdapter;
	
	ViewPager pager;
	RestaurantsPanelAdapter adapter;

	@Override
	public void onCreate(Bundle state){
		super.onCreate(state);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_restaurants);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		pager = (NonSwipeableViewPager) findViewById(R.id.restaurants_bottom_panel);
		new LoadRestaurants(this).execute();
		
		map = ((MapFragment)getFragmentManager().findFragmentById(R.id.restaurants_map)).getMap();
		Marker ntiMarker = map.addMarker(new MarkerOptions().position(new LatLng(59.33756, 18.046164999999974)));
		ntiMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.map_school_marker));
		resetMapPosition();
	}
	
	private void resetMapPosition(){
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(59.33756, 18.046164999999974), 14.0f));
	}
	
	/*private void displayRestaurants(){
		if(restaurants != null){
			
			if(map != null){
				Marker ntiMarker = map.addMarker(new MarkerOptions().position(new LatLng(59.33756, 18.046164999999974)));
				ntiMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.map_school_marker));
				for(Restaurant r : restaurants.getRestaurants()){
					//Add restaurant markers
					LatLng pos = new LatLng(r.getLat().doubleValue(), r.getLon().doubleValue());
					map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(59.33756, 18.046164999999974), 14.0f));
					Marker m = map.addMarker(new MarkerOptions()
								.position(pos));
					restaurantMarkers.add(m);
				}
				map.setOnMarkerClickListener(this);
				
				namesAdapter = new RestaurantsListAdapter(this, R.layout.listitem_restaurant, restaurants.getRestaurants());
				ListView listView = (ListView)findViewById(R.id.list_restaurants);
				listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
				listView.setAdapter(namesAdapter);
				
				listView.setOnItemClickListener(new OnItemClickListener(){
					@Override
					public void onItemClick(AdapterView<?> parent, View v,
							int pos, long id) {
						selectRestaurant(pos);
					}
				});
			}
		}
		else{
			LinearLayout l = (LinearLayout) findViewById(R.id.error_view_restaurants);
			TextView errorText = new TextView(this);
			errorText.setText(getResources().getString(R.string.error_no_restaurants_found));
			errorText.setTextColor(Color.WHITE);
			errorText.setAlpha(0.8f);
			l.addView(errorText);
		}
	}*/
	
	@Override
	public boolean onMarkerClick(Marker m) {
		int i = restaurantMarkers.indexOf(m);
		restaurantSelected(i);
		return true;
	}
	
	/*private void selectRestaurant(int index){
		ListView list = (ListView) findViewById(R.id.list_restaurants);
		list.setItemChecked(index, true);
		
		Log.d(TAG, "restaurants: " + restaurants.getRestaurants().size());
		Log.d(TAG, "markers: " + restaurantMarkers.size());
		Log.d(TAG, "listItems: " + namesAdapter.getCount());
		
		BigDecimal lat = restaurants.getRestaurants().get(index).getLat();
		BigDecimal lon = restaurants.getRestaurants().get(index).getLon();
		Log.d(TAG, "index: " + index);
		map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat.doubleValue(), lon.doubleValue())));
		boolean alreadySelected = false;
		if(selectedItemsPositions.contains(index))
			alreadySelected = true;
		if(alreadySelected){
			selectedItemsPositions.remove(selectedItemsPositions.indexOf(index));
			
			if(selectedItemsPositions.size() == 0){
				for(Marker m : restaurantMarkers){
					m.setVisible(true);
				}
			}
			else{
				restaurantMarkers.get(index).setVisible(false);
			}
		}
		else{
			selectedItemsPositions.add(index);
			((View)namesAdapter.getView(index, null, list)).setSelected(false);
			for(int i=0;i<restaurantMarkers.size();i++){
				if(selectedItemsPositions.contains(i)){
					restaurantMarkers.get(i).setVisible(true);
				}
				else{
					restaurantMarkers.get(i).setVisible(false);
				}
			}
		}
	}*/
	
	/*private class LoadRestaurants extends AsyncTask<Void, Void, Void>{
		
		private Context context;
		
		public LoadRestaurants(Context context){
			this.context = context;
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			RestaurantsDbHelper rh = new RestaurantsDbHelper(context);
			restaurants = rh.getRestaurants();
			return null;
		}
		
		protected void onPostExecute(Void result){
			displayRestaurants();
		}
	}*/
	
	@Override
	public void onBackPressed(){
		for(Marker m : restaurantMarkers){
			m.setVisible(true);
		}
	    if(pager.getCurrentItem() == 1){
	    	pager.setCurrentItem(0);
	    	resetMapPosition();
	    	return;
	    }
	    super.onBackPressed();
	    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	    resetMapPosition();
	}

	@Override
	public void restaurantSelected(int index){
		adapter.setCurrentRestaurantInfo(index);
		pager.setCurrentItem(1);
		
		LatLng markerPos = restaurantMarkers.get(index).getPosition();
		map.animateCamera(CameraUpdateFactory.newLatLngZoom(markerPos, 15.0f));
		for(int i=0;i<restaurantMarkers.size();i++){
			if(i != index){
				restaurantMarkers.get(i).setVisible(false);
			}
		}
	}
	
	private class LoadRestaurants extends AsyncTask<Void, Void, Void>{
		
		private RestaurantsActivity parent;
		private Restaurants r = new Restaurants();
		
		public LoadRestaurants(RestaurantsActivity parent){
			this.parent = parent;
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			RestaurantsDbHelper rh = new RestaurantsDbHelper(parent);
			r = rh.getRestaurants();
			return null;
		}
		
		protected void onPostExecute(Void result){
			Log.d(TAG, "postExecute");
			adapter = new RestaurantsPanelAdapter(parent, parent.getSupportFragmentManager(), parent, r);
			pager.setAdapter(adapter);
			
			for(Restaurant r : r.getRestaurants()){
				//Add restaurant markers
				LatLng pos = new LatLng(r.getLat().doubleValue(), r.getLon().doubleValue());
				Marker m = map.addMarker(new MarkerOptions()
							  .position(pos));
				restaurantMarkers.add(m);
			}
		}
	}
}
