package com.campus.campus.domain.place.application.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.campus.campus.domain.place.application.dto.response.LikeResponse;
import com.campus.campus.domain.place.application.dto.response.SavedPlaceInfo;
import com.campus.campus.domain.place.application.dto.response.naver.NaverSearchResponse;
import com.campus.campus.domain.place.domain.entity.Coordinate;
import com.campus.campus.domain.place.domain.entity.LikedPlace;
import com.campus.campus.domain.place.domain.entity.Place;
import com.campus.campus.domain.place.domain.entity.PlaceImages;
import com.campus.campus.domain.user.domain.entity.User;

@Component
public class PlaceMapper {
	public LikeResponse toLikeResponse(Place place) {
		return new LikeResponse(
			place.getPlaceId(), true
		);
	}

	public SavedPlaceInfo toSavedPlaceInfo(NaverSearchResponse.Item item, String placeName, String placeKey, String naverPlaceUrl, List<String> images) {

		return new SavedPlaceInfo(
			placeName,
			placeKey,
			item.address(),
			item.category(),
			naverPlaceUrl,
			item.telephone(),
			toCoordinate(item),
			images
		);
	}

	public Place createPlace(
		SavedPlaceInfo savedPlaceInfo, String placeName
	) {
		return Place.builder()
			.placeKey(savedPlaceInfo.placeKey())
			.placeName(placeName)
			.placeCategory(savedPlaceInfo.category())
			.phone(savedPlaceInfo.telephone())
			.address(savedPlaceInfo.address())
			.naverPlaceUrl(savedPlaceInfo.link())
			.coordinate(savedPlaceInfo.coordinate())
			.build();
	}

	public PlaceImages createPlaceImages(
		String placeKey,
		String googleImageUrl
	) {
		return PlaceImages.builder()
			.placeKey(placeKey)
			.imageUrl(googleImageUrl)
			.build();
	}

	public LikedPlace createLikedPlace(User user, Place place) {
		return LikedPlace.builder()
			.user(user)
			.place(place)
			.build();
	}

	public Coordinate toCoordinate(NaverSearchResponse.Item item) {
		return Coordinate.fromNaver(
			Double.parseDouble(item.mapx()),
			Double.parseDouble(item.mapy())
		);
	}
}
