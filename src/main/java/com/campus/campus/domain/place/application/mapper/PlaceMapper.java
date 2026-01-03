package com.campus.campus.domain.place.application.mapper;

import static com.campus.campus.domain.place.application.dto.response.SavedPlaceInfo.*;

import java.util.List;

import org.springframework.stereotype.Component;

import com.campus.campus.domain.place.application.dto.response.LikeResponse;
import com.campus.campus.domain.place.application.dto.response.SavedPlaceInfo;
import com.campus.campus.domain.place.application.dto.response.naver.NaverSearchResponse;
import com.campus.campus.domain.place.domain.entity.Coordinate;
import com.campus.campus.domain.place.domain.entity.Place;

@Component
public class PlaceMapper {

	public LikeResponse toPlaceSaveResponse(Long placeId) {
		return new LikeResponse(
			placeId
		);
	}

	public SavedPlaceInfo toSavedPlaceInfo(NaverSearchResponse.Item item, String placeKey) {

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
			List.of() // imgUrls는 나중에 Google에서 채움
		);
	}

	public Place toEntity(SavedPlaceInfo savedPlaceInfo) {

		return new Place(
			savedPlaceInfo.placeKey(),
			savedPlaceInfo.placeName(),
			savedPlaceInfo.category(),
			savedPlaceInfo.telephone(),
			savedPlaceInfo.address(),
			savedPlaceInfo.link(),
			savedPlaceInfo.coordinate()
		);
	}

	/**
	 * 태그 제거용
	 */
	public static String stripHtml(String text) {
		return text.replaceAll("<[^>]*>", "");
	}
}
