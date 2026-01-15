package com.campus.campus.domain.place.application.dto.response;

public interface PlaceDetailView {

	Long placeId();

	String placeKey();

	String name();

	String category();

	String address();

	Double latitude();

	Double longitude();

	boolean isLiked();

	double star();

	double distance();
}
