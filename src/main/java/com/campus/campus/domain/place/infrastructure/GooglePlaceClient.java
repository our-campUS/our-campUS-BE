package com.campus.campus.domain.place.infrastructure;

import java.util.List;

public interface GooglePlaceClient {
	List<String> fetchImages(String name, String address, int limit);
}
