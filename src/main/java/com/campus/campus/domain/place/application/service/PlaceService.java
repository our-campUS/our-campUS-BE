package com.campus.campus.domain.place.application.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campus.campus.domain.place.application.dto.response.LikeResponse;
import com.campus.campus.domain.place.application.dto.response.SavedPlaceInfo;
import com.campus.campus.domain.place.application.dto.response.naver.NaverSearchResponse;
import com.campus.campus.domain.place.application.mapper.PlaceMapper;
import com.campus.campus.domain.place.application.util.PlaceKeyGenerator;
import com.campus.campus.domain.place.domain.entity.LikedPlace;
import com.campus.campus.domain.place.domain.entity.Place;
import com.campus.campus.domain.place.domain.entity.PlaceImages;
import com.campus.campus.domain.place.domain.repository.LikedPlacesRepository;
import com.campus.campus.domain.place.domain.repository.PlaceImagesRepository;
import com.campus.campus.domain.place.domain.repository.PlaceRepository;
import com.campus.campus.domain.place.infrastructure.google.GooglePlaceClientImpl;
import com.campus.campus.domain.place.infrastructure.naver.NaverMapClient;
import com.campus.campus.domain.user.application.exception.UserNotFoundException;
import com.campus.campus.domain.user.domain.entity.User;
import com.campus.campus.domain.user.domain.repository.UserRepository;
import com.campus.campus.global.oci.application.dto.request.PresignedUrlRequestDto;
import com.campus.campus.global.oci.application.dto.response.PresignedUrlResponseDto;
import com.campus.campus.global.oci.application.service.PresignedUrlService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaceService {

	private final NaverMapClient naverMapClient;
	private final PlaceMapper placeMapper;
	private final PlaceRepository placeRepository;
	private final GooglePlaceClientImpl googleClient;
	private final PlaceImagesRepository placeImagesRepository;
	private final PresignedUrlService presignedUrlService;
	private final LikedPlacesRepository likedPlacesRepository;
	private final UserRepository userRepository;

	public List<SavedPlaceInfo> search(String keyword) {
		//네이버에서 특정 장소 기본정보 받아오기
		NaverSearchResponse response = naverMapClient.searchPlaces(keyword, 5);

		return response.items().stream()
			.map(item -> {

				String name = placeMapper.stripHtml(item.title());
				String address = item.roadAddress();

				//placeKey 생성
				String placeKey = PlaceKeyGenerator.generate(name, address);

				//구글 -> 이미지 가져오기
				List<String> images = getPlaceImgs(
					placeKey,
					name,
					address
				);

				//이미지도 함께 저장
				return placeMapper.toSavedPlaceInfo(item, placeKey, images);
			})
			.toList();
	}

	/*
	 * 장소 검색 시 google places로부터 이미지 불러오기
	 */
	private List<String> getPlaceImgs(String placeKey, String name, String address) {
		// DB 확인
		List<String> images = getImages(placeKey);
		if (!images.isEmpty()) {
			return images;
		}

		//최초 검색 시 google에서 이미지 url 가져오기
		List<String> googleImageUrls = googleClient.fetchImages(name, address, 3);
		if (googleImageUrls.isEmpty()) {
			return List.of();
		}
		return googleImageUrls;
	}

	/*
	 * DB 조회
	 */
	private List<String> getImages(String placeKey) {
		return placeImagesRepository.findByPlaceKey(placeKey).stream()
			.map(PlaceImages::getImageUrl)
			.toList();
	}

	@Transactional
	protected void migrateImagestoOci(String placeKey, List<String> imageUrls) {

		//google 이미지 OCI 업로드
		for (String googleUrl : imageUrls) {

			// google 이미지 다운로드
			byte[] bytes = googleClient.downloadImage(googleUrl);

			//OCI 업로드->objectKey 반환
			PresignedUrlResponseDto presigned =
				presignedUrlService.createPresignedUrl(
					"places",
					new PresignedUrlRequestDto("image/jpeg")
				);

			presignedUrlService.uploadToOci(
				presigned.uploadUrl(),
				bytes,
				"image/jpeg"
			);

			placeImagesRepository.save(
				placeMapper.createPlaceImages(placeKey, googleUrl)
			);
		}
	}

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
				PlaceMapper.createPlace(placeInfo)
			);

			//신규 생성된 경우에만 이미지 저장
			migrateImagestoOci(
				place.getPlaceKey(),
				placeInfo.imgUrls()
			);
		}
		//likedPlace 저장
		likedPlacesRepository.save(new LikedPlace(user, place));
		return new LikeResponse(place.getPlaceId(), true);
	}

}
