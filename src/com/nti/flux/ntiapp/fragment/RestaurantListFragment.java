package com.nti.flux.ntiapp.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.nti.flux.ntiapp.R;
import com.nti.flux.ntiapp.adapter.RestaurantsListAdapter;
import com.nti.flux.ntiapp.listener.OnRestaurantSelectedListener;
import com.nti.flux.ntiapp.model.Restaurant;
import com.nti.flux.ntiapp.model.Restaurants;

public class RestaurantListFragment extends Fragment{
	private static final String TAG = RestaurantListFragment.class.getName();
	
	static Restaurants restaurants;
	ArrayAdapter<Restaurant> namesAdapter;
	static OnRestaurantSelectedListener listener;

	public static RestaurantListFragment newInstance(OnRestaurantSelectedListener l, Restaurants r){
		RestaurantListFragment frag = new RestaurantListFragment();
		listener = l;
		restaurants = r;
		return frag;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state){
		View root = inflater.inflate(R.layout.fragment_restaurant_list, container, false);
		if(restaurants != null){
			namesAdapter = new RestaurantsListAdapter(this.getActivity(), R.layout.listitem_restaurant, restaurants.getRestaurants());
			ListView listView = (ListView)root.findViewById(R.id.list_restaurants);
			listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
			listView.setAdapter(namesAdapter);
			
			listView.setOnItemClickListener(new OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> parent, View v,
						int pos, long id) {
					if(listener != null){
						listener.restaurantSelected(pos);
					}
					else{
						Log.e(TAG, "Listener was null");
					}
				}
			});
		}
		return root;
	}
}
	
