package com.campus.campus.domain.place.application.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class PlaceImageCache {

	private final Map<String, List<String>> store = new ConcurrentHashMap<>();

	public List<String> get(String placeKey) {
		return store.get(placeKey);
	}

	public void put(String placeKey, List<String> images) {
		store.put(placeKey, images);
	}
}
