package com.campus.campus.domain.place.application.mapper;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.stereotype.Component;

import com.campus.campus.domain.place.application.dto.response.LikeResponse;
import com.campus.campus.domain.place.application.dto.response.SavedPlaceInfo;
import com.campus.campus.domain.place.application.dto.response.naver.NaverSearchResponse;
import com.campus.campus.domain.place.application.exception.NaverMapAPIException;
import com.campus.campus.domain.place.domain.entity.Coordinate;
import com.campus.campus.domain.place.domain.entity.LikedPlace;
import com.campus.campus.domain.place.domain.entity.Place;
import com.campus.campus.domain.place.domain.entity.PlaceImages;
import com.campus.campus.domain.user.domain.entity.User;

@Component
public class PlaceMapper {

	public String buildNaverPlaceUrl(NaverSearchResponse.Item item) {
		try {
			return String.format(
				"https://map.naver.com/v5/search/%s?c=%f,%f,15,0,0,0,dh",
				URLEncoder.encode(item.title(), StandardCharsets.UTF_8),
				Double.parseDouble(item.mapy()),
				Double.parseDouble(item.mapx())
			);
		} catch (Exception e) {
			throw new NaverMapAPIException();
		}
	}

	public LikeResponse toLikeResponse(Place place) {
		return new LikeResponse(
			place.getPlaceId(), true
		);
	}

	public SavedPlaceInfo toSavedPlaceInfo(NaverSearchResponse.Item item, String placeKey, List<String> images) {

		return new SavedPlaceInfo(
			stripHtml(item.title()),
			placeKey,
			item.address(),
			item.category(),
			buildNaverPlaceUrl(item),
			item.telephone(),
			Coordinate.fromNaver(
				Double.parseDouble(item.mapy()), //위도
				Double.parseDouble(item.mapx()) //경도
			),
			images
		);
	}

	public Place createPlace(
		SavedPlaceInfo savedPlaceInfo
	) {
		return Place.builder()
			.placeKey(savedPlaceInfo.placeKey())
			.placeName(stripHtml(savedPlaceInfo.placeName()))
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

	/**
	 * 태그 제거용
	 */
	public String stripHtml(String text) {
		return text.replaceAll("<[^>]*>", "");
	}
}
