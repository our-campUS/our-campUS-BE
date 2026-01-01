package com.campus.campus.domain.place.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campus.campus.domain.place.application.dto.response.SavedPlaceInfo;
import com.campus.campus.domain.place.domain.entity.LikedPlace;
import com.campus.campus.domain.place.domain.entity.Place;
import com.campus.campus.domain.place.domain.repository.LikedPlacesRepository;
import com.campus.campus.domain.place.domain.repository.PlaceRepository;
import com.campus.campus.domain.user.application.exception.UserNotFoundException;
import com.campus.campus.domain.user.domain.entity.User;
import com.campus.campus.domain.user.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LikePlaceService {

	private final PlaceRepository placeRepository;
	private final LikedPlacesRepository likedPlacesRepository;
	private final UserRepository userRepository;

	//장소 저장
	@Transactional
	public Long execute(SavedPlaceInfo placeInfo, Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(UserNotFoundException::new);

		//장소 저장
		Place place = Place.create(
			placeInfo.placeName(),
			placeInfo.placeKey(),
			placeInfo.category(),
			placeInfo.telephone(),
			placeInfo.address(),
			placeInfo.link(),
			placeInfo.coordinate()
		);

		//좋아요 누른 장소 저장
		likedPlacesRepository.save(new LikedPlace(user, place));
		return placeRepository.save(place).getPlaceId();
	}

}
