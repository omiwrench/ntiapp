package com.nti.flux.ntiapp.model;

import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root
public class Restaurants {

	@ElementList(type=Restaurant.class)
	private List<Restaurant> restaurantList;

	public List<Restaurant> getRestaurants() {
		return restaurantList;
	}
	public void setRestaurants(List<Restaurant> restaurantList) {
		this.restaurantList = restaurantList;
	}
}
