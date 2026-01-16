package com.campus.campus.domain.place.application.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.campus.campus.domain.council.domain.entity.CouncilType;
import com.campus.campus.domain.councilpost.domain.entity.StudentCouncilPost;
import com.campus.campus.domain.place.application.dto.response.LikeResponse;
import com.campus.campus.domain.place.application.dto.response.PartnershipPinResponse;
import com.campus.campus.domain.place.application.dto.response.PlaceDetailResponse;
import com.campus.campus.domain.place.application.dto.response.RecommendNearByPlaceResponse;
import com.campus.campus.domain.place.application.dto.response.RecommendPartnershipPlaceResponse;
import com.campus.campus.domain.place.application.dto.response.RecommendPlaceByTimeResponse;
import com.campus.campus.domain.place.application.dto.response.SavedPlaceInfo;
import com.campus.campus.domain.place.application.dto.response.SearchPartnershipInfoResponse;
import com.campus.campus.domain.place.application.dto.response.SearchPlaceInfoResponse;
import com.campus.campus.domain.place.application.dto.response.naver.NaverSearchResponse;
import com.campus.campus.domain.place.application.dto.response.partnership.PartnershipDetailResponse;
import com.campus.campus.domain.place.domain.entity.Coordinate;
import com.campus.campus.domain.place.domain.entity.LikedPlace;
import com.campus.campus.domain.place.domain.entity.Place;
import com.campus.campus.domain.place.domain.entity.PlaceImages;
import com.campus.campus.domain.review.application.dto.response.ReviewPartnerResponse;
import com.campus.campus.domain.review.application.dto.response.SimpleReviewResponse;
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

	public SearchPlaceInfoResponse toSearchPlaceInfoResponse(SavedPlaceInfo savedPlaceInfo, boolean isLiked,
		List<SearchPartnershipInfoResponse> partnerships, Double averageStar) {
		return new SearchPlaceInfoResponse(
			savedPlaceInfo.placeName(),
			savedPlaceInfo.placeKey(),
			savedPlaceInfo.address(),
			savedPlaceInfo.category(),
			savedPlaceInfo.link(),
			savedPlaceInfo.telephone(),
			savedPlaceInfo.coordinate(),
			savedPlaceInfo.imgUrls(),
			isLiked,
			partnerships,
			averageStar
		);
	}

	public PartnershipPinResponse toPartnershipPinResponse(StudentCouncilPost post, Place place) {
		return new PartnershipPinResponse(
			post.getId(),
			place.getPlaceId(),
			place.getPlaceName(),
			place.getCoordinate().latitude(),
			place.getCoordinate().longitude()
		);
	}

	public PartnershipDetailResponse toPartnershipResponse(User user, StudentCouncilPost post, Place place,
		boolean isLiked,
		List<String> imgUrls, double distance, Double averageStar, List<SimpleReviewResponse> reviews, Integer size) {
		return new PartnershipDetailResponse(
			place.getPlaceId(),
			place.getPlaceKey(),
			place.getPlaceName(),
			place.getPlaceCategory(),
			place.getAddress(),
			place.getCoordinate().latitude(),
			place.getCoordinate().longitude(),
			resolveTag(post, user),
			isLiked,
			averageStar,
			post.getTitle(),
			distance,
			post.getEndDateTime().toLocalDate(),
			imgUrls,
			reviews,
			size
		);
	}

	public PlaceDetailResponse toPlaceDetailResponse(User user, Place place, boolean isLiked,
		List<String> imgUrls, double distance, Double averageStar, List<SimpleReviewResponse> reviews, Integer size) {
		return new PlaceDetailResponse(
			place.getPlaceId(),
			place.getPlaceKey(),
			place.getPlaceName(),
			place.getPlaceCategory(),
			place.getAddress(),
			place.getCoordinate().latitude(),
			place.getCoordinate().longitude(),
			isLiked,
			averageStar,
			distance,
			imgUrls,
			reviews,
			size
		);
	}

	public ReviewPartnerResponse toReviewPartnerResponse(StudentCouncilPost post, Place place, double averageStar) {
		return new ReviewPartnerResponse(
			place.getPlaceName(),
			place.getPlaceCategory(),
			post.getWriter().getCouncilName(),
			averageStar, //리뷰 별점
			post.getTitle()
		);
	}

	public RecommendPlaceByTimeResponse toRecommendPlaceByTimeResponse(String type,
		List<RecommendPartnershipPlaceResponse> partnershipPosts, List<RecommendNearByPlaceResponse> nearbyPlaces) {

		return new RecommendPlaceByTimeResponse(type, partnershipPosts, nearbyPlaces);
	}

	public RecommendPartnershipPlaceResponse toRecommendPartnershipPlaceResponse(StudentCouncilPost post) {
		return new RecommendPartnershipPlaceResponse(
			post.getPlace().getPlaceId(),
			post.getPlace().getPlaceName(),
			post.getWriter().getCouncilName(),
			post.getTitle(),
			post.getPlace().getAddress(),
			post.getThumbnailImageUrl()
		);
	}

	public RecommendNearByPlaceResponse toRecommendNearByPlaceResponse(SavedPlaceInfo savedPlaceInfo,
		List<String> imageUrl) {
		return new RecommendNearByPlaceResponse(
			savedPlaceInfo.placeName(),
			savedPlaceInfo.placeKey(),
			savedPlaceInfo.address(),
			savedPlaceInfo.category(),
			savedPlaceInfo.link(),
			savedPlaceInfo.telephone(),
			savedPlaceInfo.coordinate(),
			imageUrl
		);
	}

	private String resolveTag(StudentCouncilPost post, User user) {
		CouncilType councilType = post.getWriter().getCouncilType();
		return switch (councilType) {
			case SCHOOL_COUNCIL -> user.getSchool().getSchoolName();
			case COLLEGE_COUNCIL -> user.getCollege().getCollegeName();
			case MAJOR_COUNCIL -> user.getMajor().getMajorName();
		};
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
