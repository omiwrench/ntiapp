package com.nti.flux.ntiapp.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.nti.flux.ntiapp.fragment.RestaurantInfoPanelFragment;
import com.nti.flux.ntiapp.fragment.RestaurantListFragment;
import com.nti.flux.ntiapp.listener.OnRestaurantSelectedListener;
import com.nti.flux.ntiapp.model.Restaurant;
import com.nti.flux.ntiapp.model.Restaurants;

public class RestaurantsPanelAdapter extends FragmentStatePagerAdapter {
	private static final String TAG = RestaurantsPanelAdapter.class.getName();
	
	Restaurants restaurants = new Restaurants();
	Restaurant currentRestaurant;
	OnRestaurantSelectedListener listener;
	RestaurantInfoPanelFragment infoFragment;
	RestaurantListFragment listFragment;
    public RestaurantsPanelAdapter(Context context, FragmentManager fm, OnRestaurantSelectedListener listener, Restaurants r) {
        super(fm);
        this.listener = listener;
        this.restaurants = r;
    }
    
    public void setCurrentRestaurantInfo(int index){
    	try{
    		infoFragment.restaurantChanged(restaurants.getRestaurants().get(index));
    	}
    	catch(NullPointerException e){
    		Log.e(TAG, "NullPointerException in Restaurants adapter");
    	}
    }

    @Override
    public Fragment getItem(int pos){
    	Log.d(TAG, "getitem called");
        if(pos == 0 && restaurants != null){
        	listFragment = RestaurantListFragment.newInstance(listener, restaurants);
        	return listFragment;
        }
        else if(pos == 1){
        	infoFragment = RestaurantInfoPanelFragment.newInstance();
        	return infoFragment;
        }
        else{
        	return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
