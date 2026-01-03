package com.campus.campus.domain.place.application.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campus.campus.domain.place.application.dto.response.LikeResponse;
import com.campus.campus.domain.place.application.dto.response.SavedPlaceInfo;
import com.campus.campus.domain.place.application.mapper.PlaceMapper;
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
	private final PlaceMapper placeMapper;
	private final PlaceImagesService placeImagesService;

	//장소 저장
	@Transactional
	public LikeResponse likePlace(SavedPlaceInfo placeInfo, Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(UserNotFoundException::new);

		String placeKey = placeInfo.placeKey();

		//이미 좋아요가 존재하는지 확인
		Optional<LikedPlace> likedPlace =
			likedPlacesRepository.findByUserIdAndPlaceKey(userId, placeKey);

		if (likedPlace.isPresent()) {
			//이미 좋아요 상태->좋아요 취소
			likedPlacesRepository.delete(likedPlace.get());
			return new LikeResponse(null, false);
		}

		//Place 엔티티 생성
		Place place;
		Optional<Place> optionalPlace = placeRepository.findByPlaceKey(placeKey);

		if (optionalPlace.isPresent()) {
			//기존 place 존재->그대로 사용
			place = optionalPlace.get();
		} else {
			//place 신규 생성
			place = placeRepository.save(
				placeMapper.toEntity(placeInfo)
			);

			//신규 생성된 경우에만 이미지 저장
			placeImagesService.migrateImagestoOci(
				place.getPlaceKey(),
				placeInfo.imgUrls()
			);
		}
		//likedPlace 저장
		likedPlacesRepository.save(new LikedPlace(user, place));
		return new LikeResponse(place.getPlaceId(), true);
	}

}
