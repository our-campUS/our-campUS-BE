package com.campus.campus.domain.place.application.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campus.campus.domain.councilpost.application.exception.AcademicInfoNotSetException;
import com.campus.campus.domain.councilpost.domain.entity.StudentCouncilPost;
import com.campus.campus.domain.councilpost.domain.entity.ThumbnailIcon;
import com.campus.campus.domain.councilpost.domain.repository.StudentCouncilPostRepository;
import com.campus.campus.domain.place.application.dto.response.LikeResponse;
import com.campus.campus.domain.place.application.dto.response.RecommendNearByPlaceResponse;
import com.campus.campus.domain.place.application.dto.response.RecommendPartnershipPlaceResponse;
import com.campus.campus.domain.place.application.dto.response.RecommendPlaceByTimeResponse;
import com.campus.campus.domain.place.application.dto.response.SavedPlaceInfo;
import com.campus.campus.domain.place.application.dto.response.SearchCandidateResponse;
import com.campus.campus.domain.place.application.dto.response.geocoder.AddressResponse;
import com.campus.campus.domain.place.application.dto.response.naver.NaverSearchResponse;
import com.campus.campus.domain.place.application.exception.NaverMapAPIException;
import com.campus.campus.domain.place.application.exception.PlaceCreationException;
import com.campus.campus.domain.place.application.mapper.PlaceMapper;
import com.campus.campus.domain.place.application.util.PlaceKeyGenerator;
import com.campus.campus.domain.place.domain.entity.Coordinate;
import com.campus.campus.domain.place.domain.entity.LikedPlace;
import com.campus.campus.domain.place.domain.entity.Place;
import com.campus.campus.domain.place.domain.entity.PlaceImages;
import com.campus.campus.domain.place.domain.repository.LikedPlacesRepository;
import com.campus.campus.domain.place.domain.repository.PlaceImagesRepository;
import com.campus.campus.domain.place.domain.repository.PlaceRepository;
import com.campus.campus.domain.place.infrastructure.geocoder.GeoCoderClient;
import com.campus.campus.domain.place.infrastructure.google.GooglePlaceClient;
import com.campus.campus.domain.place.infrastructure.naver.NaverMapClient;
import com.campus.campus.domain.user.application.exception.UserNotFoundException;
import com.campus.campus.domain.user.domain.entity.User;
import com.campus.campus.domain.user.domain.repository.UserRepository;
import com.campus.campus.global.oci.application.dto.request.PresignedUrlRequestDto;
import com.campus.campus.global.oci.application.dto.response.PresignedUrlResponseDto;
import com.campus.campus.global.oci.application.service.PresignedUrlService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlaceService {

	private static final LocalTime LUNCH_START = LocalTime.of(11, 30);
	private static final LocalTime LUNCH_END = LocalTime.of(14, 0);
	private static final LocalTime CAFE_START = LocalTime.of(14, 0);
	private static final LocalTime CAFE_END = LocalTime.of(17, 0);
	private static final LocalTime DINNER_START = LocalTime.of(17, 0);
	private static final LocalTime DINNER_END = LocalTime.of(20, 0);
	private static final LocalTime BAR_START = LocalTime.of(20, 0);
	private static final LocalTime BAR_END = LocalTime.of(23, 30);
	private static final ZoneId KST = ZoneId.of("Asia/Seoul");

	private final NaverMapClient naverMapClient;
	private final PlaceMapper placeMapper;
	private final PlaceRepository placeRepository;
	private final GooglePlaceClient googleClient;
	private final StudentCouncilPostRepository studentCouncilPostRepository;
	private final PlaceImagesRepository placeImagesRepository;
	private final PresignedUrlService presignedUrlService;
	private final RedisPlaceCacheService redisPlaceCacheService;
	private final LikedPlacesRepository likedPlacesRepository;
	private final UserRepository userRepository;
	private final ExecutorService executorService;
	private final GeoCoderClient geoCoderClient;

	public List<SavedPlaceInfo> searchByLocationAndKeyword(double lat, double lng, String keyword, int imageLimit) {
		String searchWord = keyword;

		try {
			AddressResponse addressResponse = geoCoderClient.getAddress(lat, lng);
			String nowAddress = toStringAddress(addressResponse);

			if (nowAddress != null && !nowAddress.isBlank()) {
				searchWord = nowAddress + " " + keyword;
				log.info("nowAddress={}", nowAddress);
			}
		} catch (Exception e) {
			log.warn("지오코딩 변환 실패 (좌표: {}, {}). 사유: {}", lat, lng, e.getMessage());
		}

		log.info("최종 검색어(searchWord)={}", searchWord);

		//네이버에서 특정 장소 기본정보 받아오기
		NaverSearchResponse naverSearchResponse = naverMapClient.searchPlaces(searchWord, 5);

		return processSearchResults(naverSearchResponse, imageLimit);
	}

	public List<SavedPlaceInfo> searchByKeyword(String keyword, int imageLimit) {
		NaverSearchResponse naverSearchResponse = naverMapClient.searchPlaces(keyword, 5);

		return processSearchResults(naverSearchResponse, imageLimit);
	}

	public Place findOrCreatePlace(SavedPlaceInfo place) {
		String placeKey = place.placeKey();

		return placeRepository.findByPlaceKey(placeKey)
			.orElseGet(() -> {
				try {
					Place newPlace = placeRepository.save(placeMapper.createPlace(place));

					migrateImagesToOci(newPlace.getPlaceKey(), place.imgUrls());

					return newPlace;
				} catch (DataIntegrityViolationException e) {
					log.info("해당 키에 대한 장소 동시 생성이 감지되었습니다.: {}", placeKey);
					return placeRepository.findByPlaceKey(placeKey)
						.orElseThrow(PlaceCreationException::new);
				}
			});
	}

	//장소 저장
	@Transactional
	public LikeResponse likePlace(SavedPlaceInfo placeInfo, Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(UserNotFoundException::new);

		String placeKey = placeInfo.placeKey();

		//이미 좋아요가 존재하는지 확인
		Optional<LikedPlace> likedPlace = likedPlacesRepository.findByUserIdAndPlace_PlaceKey(userId, placeKey);

		if (likedPlace.isPresent()) {
			//이미 좋아요 상태->좋아요 취소
			likedPlacesRepository.delete(likedPlace.get());
			return new LikeResponse(null, false);
		}

		//Place 엔티티 생성
		Place place;
		try {
			//조회
			place = placeRepository.findByPlaceKey(placeKey)
				.orElseGet(() -> {
					//없으면 생성
					String placeName = stripHtml(placeInfo.placeName());
					Place newPlace = placeRepository.save(placeMapper.createPlace(placeInfo));
					//신규 생성된 경우에만 이미지 저장
					migrateImagesToOci(newPlace.getPlaceKey(), placeInfo.imgUrls());

					return newPlace;
				});
		} catch (DataIntegrityViolationException e) {
			//동시 생성으로 unique 제약 위반 시 다시 조회
			place = placeRepository.findByPlaceKey(placeKey)
				.orElseThrow(PlaceCreationException::new);
		}
		//likedPlace 저장
		LikedPlace savedLikedPlace = placeMapper.createLikedPlace(user, place);
		likedPlacesRepository.save(savedLikedPlace);

		return placeMapper.toLikeResponse(place);
	}

	@Transactional(readOnly = true)
	public RecommendPlaceByTimeResponse findRecommendations(Long userId, double lat, double lng) {
		LocalTime now = LocalTime.now(KST);
		User user = userRepository.findByIdAndDeletedAtIsNull(userId)
			.orElseThrow(UserNotFoundException::new);

		if (user.isProfileNotCompleted()) {
			throw new AcademicInfoNotSetException();
		}

		if (isLunchTime(now)) {
			return generateResponse(user, lat, lng, ThumbnailIcon.FOOD, "식당", "LUNCH");
		} else if (isCafeTime(now)) {
			return generateResponse(user, lat, lng, ThumbnailIcon.CAFE, "카페", "CAFE");
		} else if (isDinnerTime(now)) {
			return generateResponse(user, lat, lng, ThumbnailIcon.FOOD, "식당", "DINNER");
		} else if (isBarTime(now)) {
			return generateResponse(user, lat, lng, ThumbnailIcon.BAR, "술집", "BAR");
		} else {
			return placeMapper.toRecommendPlaceByTimeResponse("잠잘시간입니다.", List.of(), List.of());
		}
	}

	private List<SavedPlaceInfo> processSearchResults(NaverSearchResponse naverSearchResponse, int imageLimit) {
		List<SearchCandidateResponse> candidates = naverSearchResponse.items().stream()
			.map(item -> {
				String name = stripHtml(item.title());
				String address = item.roadAddress();
				String placeKey = PlaceKeyGenerator.generate(name, address);
				String naverPlaceUrl = buildNaverPlaceUrl(item);
				return new SearchCandidateResponse(item, name, address, placeKey, naverPlaceUrl);
			})
			.toList();

		List<String> placeKeys = candidates.stream()
			.map(SearchCandidateResponse::placeKey)
			.distinct()
			.toList();

		Map<String, List<String>> images = placeImagesRepository.findAllByPlaceKeyIn(placeKeys).stream()
			.collect(Collectors.groupingBy(
				PlaceImages::getPlaceKey,
				Collectors.mapping(PlaceImages::getImageUrl, Collectors.toList())
			));

		List<CompletableFuture<SavedPlaceInfo>> futures = candidates.stream()
			.map(response -> CompletableFuture.supplyAsync(() -> convertToSavedPlaceInfo(response, images, imageLimit),
					executorService)
				.completeOnTimeout(fallback(response), 4, TimeUnit.SECONDS)
				.exceptionally(ex -> fallback(response)))
			.toList();

		CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
		return futures.stream().map(CompletableFuture::join).toList();
	}

	private SavedPlaceInfo convertToSavedPlaceInfo(SearchCandidateResponse response, Map<String, List<String>> images,
		int imageLimit) {
		List<String> cached = images.getOrDefault(response.placeKey(), List.of());
		List<String> placeImages = !cached.isEmpty()
			? cached : googleClient.fetchImages(response.name(), response.address(), imageLimit);

		return placeMapper.toSavedPlaceInfo(response.item(), response.name(), response.placeKey(),
			response.naverPlaceUrl(), placeImages == null ? List.of() : placeImages
		);
	}

	private SavedPlaceInfo fallback(SearchCandidateResponse response) {
		return placeMapper.toSavedPlaceInfo(response.item(), response.name(), response.placeKey(),
			response.naverPlaceUrl(), List.of());
	}

	/*
	 * 태그 제거용
	 */
	private String stripHtml(String text) {
		return text.replaceAll("<[^>]*>", "");
	}

	private String buildNaverPlaceUrl(NaverSearchResponse.Item item) {
		String link = item.link();
		if (link != null && !link.isBlank()) {
			String normalized = normalizeNaverMapLink(link);
			if (normalized != null) {
				return normalized;
			}
		}

		try {
			Coordinate coordinate = placeMapper.toCoordinate(item);
			return String.format(
				"https://map.naver.com/v5/search/%s?c=%f,%f,15,0,0,0,dh",
				URLEncoder.encode(stripHtml(item.title()), StandardCharsets.UTF_8),
				coordinate.latitude(),
				coordinate.longitude()
			);
		} catch (Exception e) {
			throw new NaverMapAPIException();
		}
	}

	private String normalizeNaverMapLink(String link) {
		String trimmed = link.trim();
		if (trimmed.contains("naver.com") || trimmed.contains("naver.me")) {
			return trimmed.replace("http://", "https://");
		}
		return null;
	}

	private void migrateImagesToOci(String placeKey, List<String> imageUrls) {
		if (imageUrls == null || imageUrls.isEmpty()) {
			return;
		}

		//google 이미지 OCI 업로드
		for (String googleUrl : imageUrls) {
			try {
				// google 이미지 다운로드
				byte[] bytes = googleClient.downloadImage(googleUrl);

				//OCI 업로드->objectKey 반환
				PresignedUrlResponseDto presignedUrlResponseDto =
					presignedUrlService.createPresignedUrl(
						"places",
						new PresignedUrlRequestDto("image/jpeg")
					);

				presignedUrlService.uploadToOci(presignedUrlResponseDto.uploadUrl(), bytes, "image/jpeg");

				PlaceImages placeImages = placeMapper.createPlaceImages(placeKey, presignedUrlResponseDto.imageUrl());
				placeImagesRepository.save(placeImages);
			} catch (Exception e) {
				log.warn("이미지를 OCI에 업로드하여 저장하는 것을 실패했어요. placeKey={},imageUrl={}", placeKey, googleUrl, e);
			}

		}
	}

	private String toStringAddress(AddressResponse nowAddress) {
		return nowAddress.getResponse().getResult().stream()
			.filter(r -> "road".equalsIgnoreCase(r.getType()) || "parcel".equalsIgnoreCase(r.getType()))
			.findFirst()
			.map(AddressResponse.Result::getText)
			.orElse(null);
	}

	private boolean isLunchTime(LocalTime now) {
		return !now.isBefore(LUNCH_START) && now.isBefore(LUNCH_END);
	}

	private boolean isCafeTime(LocalTime now) {
		return !now.isBefore(CAFE_START) && now.isBefore(CAFE_END);
	}

	private boolean isDinnerTime(LocalTime now) {
		return !now.isBefore(DINNER_START) && now.isBefore(DINNER_END);
	}

	private boolean isBarTime(LocalTime now) {
		return !now.isBefore(BAR_START) && now.isBefore(BAR_END);
	}

	private RecommendPlaceByTimeResponse generateResponse(User user, double lat, double lng, ThumbnailIcon icon,
		String keyword, String type) {
		List<RecommendPartnershipPlaceResponse> partnerships = getRandomPartnerships(user, icon);

		List<RecommendNearByPlaceResponse> externalPlaces = getRandomNearByPlaces(lat, lng, keyword);

		return placeMapper.toRecommendPlaceByTimeResponse(type, partnerships, externalPlaces);
	}

	private List<RecommendPartnershipPlaceResponse> getRandomPartnerships(User user, ThumbnailIcon icon) {
		Long schoolId = user.getSchool().getSchoolId();
		Long collegeId = user.getCollege() != null ? user.getCollege().getCollegeId() : null;
		Long majorId = user.getMajor() != null ? user.getMajor().getMajorId() : null;

		int poolSize = 15;
		List<StudentCouncilPost> posts = studentCouncilPostRepository.findRandomPartnershipPlace(
			schoolId, collegeId, majorId, icon, LocalDateTime.now(KST), PageRequest.of(0, poolSize)
		);

		if (posts.isEmpty()) {
			return List.of();
		}

		List<StudentCouncilPost> mutablePosts = new ArrayList<>(posts);
		Collections.shuffle(mutablePosts);

		return mutablePosts.stream()
			.limit(2)
			.map(placeMapper::toRecommendPartnershipPlaceResponse)
			.toList();
	}

	private List<RecommendNearByPlaceResponse> getRandomNearByPlaces(double lat, double lng, String keyword) {
		Optional<List<SavedPlaceInfo>> cachedPlaces = redisPlaceCacheService.getCachedPlaces(lat, lng, keyword);

		List<SavedPlaceInfo> searchResults;

		if (cachedPlaces.isPresent()) {
			searchResults = cachedPlaces.get();
		} else {
			searchResults = searchByLocationAndKeyword(lat, lng, keyword, 1);
			if (!searchResults.isEmpty()) {
				redisPlaceCacheService.cachePlaces(keyword, lat, lng, searchResults);
			}
		}

		if (searchResults.isEmpty()) {
			return List.of();
		}

		List<SavedPlaceInfo> mutableList = new ArrayList<>(searchResults);
		Collections.shuffle(mutableList);

		return mutableList.stream()
			.limit(2)
			.map(info -> {
				List<String> imageUrl = (info.imgUrls() != null && !info.imgUrls().isEmpty())
					? List.of(info.imgUrls().get(0))
					: Collections.emptyList();

				return placeMapper.toRecommendNearByPlaceResponse(info, imageUrl);
			})
			.toList();
	}

}
