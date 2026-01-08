package com.campus.campus.domain.place.application.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.campus.campus.domain.place.application.dto.response.LikeResponse;
import com.campus.campus.domain.place.application.dto.response.SavedPlaceInfo;
import com.campus.campus.domain.place.application.dto.response.geocoder.AddressResponse;
import com.campus.campus.domain.place.application.dto.response.naver.NaverSearchResponse;
import com.campus.campus.domain.place.application.dto.response.partnership.PartnershipPlaceSummary;
import com.campus.campus.domain.place.application.dto.response.partnership.PartnershipResponse;
import com.campus.campus.domain.place.application.dto.response.partnership.PartnershipScrollResponse;
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

	public SavedPlaceInfo toSavedPlaceInfo(NaverSearchResponse.Item item, String placeName, String placeKey,
		String naverPlaceUrl, List<String> images) {
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

	public PartnershipScrollResponse toPartnershipScrollResponse(List<PartnershipResponse> items, boolean hasNext,
		Long nextCursor) {
		return new PartnershipScrollResponse(items, hasNext, nextCursor);
	}

	public PartnershipResponse toPartnershipResponse(PartnershipPlaceSummary summary, List<String> tags,
		boolean isLiked, List<String> imgUrls) {
		return new PartnershipResponse(
			summary.placeId(),
			summary.placeKey(),
			summary.name(),
			summary.category(),
			summary.address(),
			summary.latitude(),
			summary.longitude(),
			tags,
			isLiked,
			imgUrls
		);
	}

	public Place createPlace(SavedPlaceInfo savedPlaceInfo) {
		return Place.builder()
			.placeKey(savedPlaceInfo.placeKey())
			.placeName(savedPlaceInfo.placeName())
			.placeCategory(savedPlaceInfo.category())
			.phone(savedPlaceInfo.telephone())
			.address(savedPlaceInfo.address())
			.naverPlaceUrl(savedPlaceInfo.link())
			.coordinate(savedPlaceInfo.coordinate())
			.build();
	}

	public String toStringAddress(AddressResponse nowAddress) {
		return nowAddress.getResponse().getResult().stream()
			.filter(r -> "road".equalsIgnoreCase(r.getType()) || "parcel".equalsIgnoreCase(r.getType()))
			.findFirst()
			.map(AddressResponse.Result::getText)
			.orElse(null);
	}

	public PlaceImages createPlaceImages(String placeKey, String googleImageUrl) {
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
