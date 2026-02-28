package com.campus.campus.domain.place.application.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campus.campus.domain.council.domain.entity.CouncilType;
import com.campus.campus.domain.council.domain.entity.StudentCouncil;
import com.campus.campus.domain.council.domain.repository.StudentCouncilRepository;
import com.campus.campus.domain.councilpost.application.exception.AcademicInfoNotSetException;
import com.campus.campus.domain.councilpost.domain.entity.PostCategory;
import com.campus.campus.domain.councilpost.domain.entity.StudentCouncilPost;
import com.campus.campus.domain.councilpost.domain.entity.ThumbnailIcon;
import com.campus.campus.domain.councilpost.domain.repository.StudentCouncilPostRepository;
import com.campus.campus.domain.place.application.dto.response.LikeResponse;
import com.campus.campus.domain.place.application.dto.response.RecommendNearByPlaceResponse;
import com.campus.campus.domain.place.application.dto.response.RecommendPartnershipPlaceResponse;
import com.campus.campus.domain.place.application.dto.response.RecommendPlaceByTimeResponse;
import com.campus.campus.domain.place.application.dto.response.SavedPlaceInfo;
import com.campus.campus.domain.place.application.dto.response.SearchCandidateResponse;
import com.campus.campus.domain.place.application.dto.response.SearchPartnershipInfoResponse;
import com.campus.campus.domain.place.application.dto.response.SearchPlaceInfoResponse;
import com.campus.campus.domain.place.application.dto.response.kakao.KakaoSearchResponse;
import com.campus.campus.domain.place.application.dto.response.partnership.PartnershipDetailResponse;
import com.campus.campus.domain.place.application.exception.AlreadySuggestedPartnershipException;
import com.campus.campus.domain.place.application.exception.PlaceCreationException;
import com.campus.campus.domain.place.application.mapper.PlaceMapper;
import com.campus.campus.domain.place.domain.entity.CouncilPartnershipSuggestion;
import com.campus.campus.domain.place.domain.entity.LikedPlace;
import com.campus.campus.domain.place.domain.entity.Place;
import com.campus.campus.domain.place.domain.entity.UserPartnershipSuggestion;
import com.campus.campus.domain.place.domain.repository.CouncilPartnershipSuggestionRepository;
import com.campus.campus.domain.place.domain.repository.LikedPlacesRepository;
import com.campus.campus.domain.place.domain.repository.PlaceRepository;
import com.campus.campus.domain.place.domain.repository.UserPartnershipSuggestionRepository;
import com.campus.campus.domain.councilpost.domain.repository.PostImageRepository;
import com.campus.campus.domain.place.infrastructure.kakao.KakaoLocalClient;
import com.campus.campus.domain.review.application.dto.response.SimpleReviewResponse;
import com.campus.campus.domain.review.application.mapper.ReviewMapper;
import com.campus.campus.domain.review.domain.entity.Review;
import com.campus.campus.domain.review.domain.entity.ReviewImage;
import com.campus.campus.domain.review.domain.repository.ReviewImageRepository;
import com.campus.campus.domain.review.domain.repository.ReviewRepository;
import com.campus.campus.domain.user.application.exception.UserNotFoundException;
import com.campus.campus.domain.user.domain.entity.User;
import com.campus.campus.domain.user.domain.repository.UserRepository;
import com.campus.campus.global.util.geocoder.GeoUtil;

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

	private final KakaoLocalClient kakaoLocalClient;
	private final PlaceMapper placeMapper;
	private final PlaceRepository placeRepository;
	private final StudentCouncilPostRepository studentCouncilPostRepository;
	private final RedisPlaceCacheService redisPlaceCacheService;
	private final LikedPlacesRepository likedPlacesRepository;
	private final UserRepository userRepository;
	private final ExecutorService executorService;
	private final ReviewRepository reviewRepository;
	private final UserPartnershipSuggestionRepository userPartnershipSuggestionRepository;
	private final CouncilPartnershipSuggestionRepository partnershipSuggestionRepository;
	private final StudentCouncilRepository studentCouncilRepository;
	private final ReviewImageRepository reviewImageRepository;
	private final PostImageRepository postImageRepository;
	private final ReviewMapper reviewMapper;

	public List<SavedPlaceInfo> searchByLocationAndKeyword(double lat, double lng, String keyword, int imageLimit) {
		log.info("카카오 좌표 기반 검색: lat={}, lng={}, keyword={}", lat, lng, keyword);

		KakaoSearchResponse kakaoSearchResponse = kakaoLocalClient.searchPlaces(keyword, lat, lng, 2000, 5);
		return processSearchResults(kakaoSearchResponse);
	}

	public List<SearchPlaceInfoResponse> searchByLocationAndKeywordWithInfo(Long userId, double lat, double lng,
		String keyword, int imageLimit) {
		List<SavedPlaceInfo> basicResults = searchByLocationAndKeyword(lat, lng, keyword, imageLimit);

		if (basicResults.isEmpty()) {
			return List.of();
		}

		List<String> placeKeys = basicResults.stream()
			.map(SavedPlaceInfo::placeKey)
			.toList();

		Set<String> likedKeys = (userId != null)
			? likedPlacesRepository.findLikedPlaceKeys(userId, placeKeys)
			: Collections.emptySet();

		Map<String, Double> starMap = reviewRepository.findAverageStarsByPlaceKeys(placeKeys).stream()
			.collect(Collectors.toMap(
				obj -> (String)obj[0],
				obj -> {
					Double val = (Double)obj[1];
					return BigDecimal.valueOf(val != null ? val : 0.0)
						.setScale(1, RoundingMode.HALF_UP)
						.doubleValue();
				})
			);

		Map<String, List<SearchPartnershipInfoResponse>> partnershipMap = studentCouncilPostRepository
			.findActivePartnershipsByPlaceKeys(placeKeys, LocalDateTime.now(KST)).stream()
			.collect(Collectors.groupingBy(
				obj -> (String)obj[0],
				Collectors.mapping(
					obj -> new SearchPartnershipInfoResponse((Long)obj[3], (String)obj[1], (String)obj[2]),
					Collectors.toList()
				)
			));

		// 제휴 장소 이미지: postId → List<imageUrl>
		Set<Long> postIds = partnershipMap.values().stream()
			.flatMap(List::stream)
			.map(SearchPartnershipInfoResponse::postId)
			.collect(Collectors.toSet());

		Map<Long, List<String>> postImageMap = postIds.isEmpty()
			? Collections.emptyMap()
			: postImageRepository.findImageUrlsByPostIds(postIds).stream()
				.collect(Collectors.groupingBy(
					obj -> (Long)obj[0],
					Collectors.mapping(obj -> (String)obj[1], Collectors.toList())
				));

		// 비제휴 DB 장소 이미지: placeId → imageUrl (1장)
		Set<Long> nonPartnershipPlaceIds = basicResults.stream()
			.filter(info -> info.placeId() != null && !partnershipMap.containsKey(info.placeKey()))
			.map(SavedPlaceInfo::placeId)
			.collect(Collectors.toSet());

		Map<Long, String> reviewImageMap = nonPartnershipPlaceIds.isEmpty()
			? Collections.emptyMap()
			: reviewImageRepository.findFirstImageUrlsByPlaceIds(nonPartnershipPlaceIds).stream()
				.collect(Collectors.toMap(obj -> (Long)obj[0], obj -> (String)obj[1]));

		return basicResults.stream()
			.map(info -> {
				List<SearchPartnershipInfoResponse> partnerships = partnershipMap.getOrDefault(info.placeKey(),
					List.of());

				List<String> imgUrls;
				if (!partnerships.isEmpty()) {
					// 제휴 장소 → PostImage 전체
					imgUrls = partnerships.stream()
						.map(SearchPartnershipInfoResponse::postId)
						.flatMap(postId -> postImageMap.getOrDefault(postId, List.of()).stream())
						.toList();
				} else if (info.placeId() != null) {
					// DB 장소 → ReviewImage 1장
					String reviewImgUrl = reviewImageMap.get(info.placeId());
					imgUrls = reviewImgUrl != null ? List.of(reviewImgUrl) : List.of();
				} else {
					// 미저장 장소 → 빈 배열
					imgUrls = List.of();
				}

				return placeMapper.toSearchPlaceInfoResponse(
					info, likedKeys.contains(info.placeKey()), partnerships,
					Math.round(starMap.getOrDefault(info.placeKey(), 0.0) * 10.0) / 10.0,
					imgUrls
				);
			})
			.toList();
	}

	public List<SavedPlaceInfo> searchByKeyword(String keyword, int imageLimit) {
		KakaoSearchResponse kakaoSearchResponse = kakaoLocalClient.searchPlaces(keyword, 5);

		return processSearchResults(kakaoSearchResponse);
	}

	public Place findOrCreatePlace(SavedPlaceInfo place) {
		String placeKey = place.placeKey();

		return placeRepository.findByPlaceKey(placeKey)
			.orElseGet(() -> {
				try {
					Place newPlace = placeRepository.save(placeMapper.createPlace(place));

					return newPlace;
				} catch (DataIntegrityViolationException e) {
					log.info("해당 키에 대한 장소 동시 생성이 감지되었습니다.: {}", placeKey);
					return placeRepository.findByPlaceKey(placeKey)
						.orElseThrow(PlaceCreationException::new);
				}
			});
	}

	public Place createPlace(SavedPlaceInfo place) {
		try {
			Place newPlace = placeRepository.save(placeMapper.createPlace(place));

			return newPlace;
		} catch (DataIntegrityViolationException e) {
			log.info("해당 키에 대한 장소 동시 생성이 감지되었습니다.: {}", place.placeKey());
			return placeRepository.findByPlaceKey(place.placeKey())
				.orElseThrow(PlaceCreationException::new);
		}
	}

	//제휴 신청
	@Transactional
	public void suggestPartnership(Long userId, SavedPlaceInfo placeInfo) {
		User user = userRepository.findById(userId)
			.orElseThrow(UserNotFoundException::new);

		Place place = findOrCreatePlace(placeInfo);

		//이미 신청했는지 체크
		if (userPartnershipSuggestionRepository
			.existsByUserAndPlace(user, place)) {
			throw new AlreadySuggestedPartnershipException();
		}

		//중복 방지 저장
		userPartnershipSuggestionRepository.save(
			UserPartnershipSuggestion.create(user, place)
		);

		//유저 소속 studentCouncil
		List<StudentCouncil> councils = resolveCouncils(user);

		// demand 조회 or 생성
		for (StudentCouncil council : councils) {
			CouncilPartnershipSuggestion demand =
				partnershipSuggestionRepository.findForUpdate(place, council)
					.orElseGet(() ->
						partnershipSuggestionRepository.save(
							CouncilPartnershipSuggestion.create(place, council)
						));
			demand.increase();
		}

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
					Place newPlace = placeRepository.save(placeMapper.createPlace(placeInfo));

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

	private List<SavedPlaceInfo> processSearchResults(KakaoSearchResponse kakaoSearchResponse) {
		List<SearchCandidateResponse> candidates = kakaoSearchResponse.documents().stream()
			.map(document -> {
				String name = document.placeName();
				String address = (document.roadAddressName() != null && !document.roadAddressName().isBlank())
					? document.roadAddressName()
					: document.addressName();
				String placeKey = document.id();
				String placeUrl = document.placeUrl();
				return new SearchCandidateResponse(document, name, address, placeKey, placeUrl);
			})
			.toList();

		List<String> placeKeys = candidates.stream()
			.map(SearchCandidateResponse::placeKey)
			.distinct()
			.toList();

		Map<String, Long> placeIdMap = placeRepository.findByPlaceKeyIn(placeKeys).stream()
			.collect(Collectors.toMap(Place::getPlaceKey, Place::getPlaceId));

		List<CompletableFuture<SavedPlaceInfo>> futures = candidates.stream()
			.map(response -> CompletableFuture.supplyAsync(
					() -> convertToSavedPlaceInfo(response, placeIdMap),
					executorService)
				.completeOnTimeout(fallback(response, placeIdMap), 4, TimeUnit.SECONDS)
				.exceptionally(ex -> fallback(response, placeIdMap)))
			.toList();

		CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
		return futures.stream().map(CompletableFuture::join).toList();
	}

	private SavedPlaceInfo convertToSavedPlaceInfo(SearchCandidateResponse response, Map<String, Long> placeIdMap) {
		Long placeId = placeIdMap.get(response.placeKey());

		return placeMapper.toSavedPlaceInfo(response.document(), response.name(), response.placeKey(),
			response.placeUrl(), List.of(),
			placeId
		);
	}

	private SavedPlaceInfo fallback(SearchCandidateResponse response, Map<String, Long> placeIdMap) {
		Long placeId = placeIdMap.get(response.placeKey());

		return placeMapper.toSavedPlaceInfo(response.document(), response.name(), response.placeKey(),
			response.placeUrl(), List.of(), placeId);
	}

	private List<StudentCouncil> resolveCouncils(User user) {
		List<StudentCouncil> councils = new ArrayList<>();

		if (user.getMajor() != null) {
			studentCouncilRepository.findByMajor_MajorIdAndDeletedAtIsNull(
				user.getMajor().getMajorId()
			).ifPresent(councils::add);
		}

		if (user.getCollege() != null) {
			studentCouncilRepository.findByCollege_CollegeIdAndDeletedAtIsNull(
				user.getCollege().getCollegeId()
			).ifPresent(councils::add);
		}

		if (user.getSchool() != null) {
			studentCouncilRepository.findBySchool_SchoolIdAndDeletedAtIsNull(
				user.getSchool().getSchoolId()
			).ifPresent(councils::add);
		}

		return councils;
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

	@Transactional(readOnly = true)
	public List<PartnershipDetailResponse> searchDetailedPlaces(
		Long userId,
		double lat,
		double lng,
		String keyword
	) {
		// 1. 외부 API(네이버/구글)를 통해 장소 검색 (기존 로직 활용, 이미지 3장 제한)
		List<SavedPlaceInfo> searchResults = searchByLocationAndKeyword(lat, lng, keyword, 3);

		if (searchResults.isEmpty()) {
			return List.of();
		}

		// 2. 검색된 placeKey 추출 및 DB 장소 일괄 조회
		List<String> placeKeys = searchResults.stream()
			.map(SavedPlaceInfo::placeKey)
			.toList();

		Map<String, Place> dbPlaceMap = placeRepository.findAllByPlaceKeyIn(placeKeys).stream()
			.collect(Collectors.toMap(Place::getPlaceKey, Function.identity()));

		// 3. 좋아요 여부 일괄 조회
		Set<String> likedPlaceKeys = (userId != null)
			? likedPlacesRepository.findLikedPlaceKeys(userId, placeKeys)
			: Collections.emptySet();

		LocalDateTime now = LocalDateTime.now();

		// 4. 결과 조립
		return searchResults.stream().map(info -> {
			String key = info.placeKey();
			Place dbPlace = dbPlaceMap.get(key);

			boolean isLiked = likedPlaceKeys.contains(key);
			Double avgStar = 0.0;
			List<SimpleReviewResponse> reviews = Collections.emptyList();
			StudentCouncilPost activePost = null;

			// DB에 저장된 장소인 경우 상세 정보 조회
			if (dbPlace != null) {
				Long placeId = dbPlace.getPlaceId();

				// 4-1. 평점 조회 (Repository 직접 호출)
				avgStar = reviewRepository.findAverageStarByPlaceId(placeId).orElse(0.0);

				// 4-2. 최신 리뷰 3개 조회 (Repository 직접 호출)
				List<Review> topReviews = reviewRepository.findTop3ByPlace_PlaceIdOrderByCreatedAtDesc(placeId);

				if (!topReviews.isEmpty()) {
					List<Long> reviewIds = topReviews.stream().map(Review::getId).toList();
					// 리뷰 이미지 조회
					Map<Long, String> imageMap = reviewImageRepository.findAllByReviewIdInOrderByIdAsc(reviewIds)
						.stream()
						.collect(Collectors.toMap(
							img -> img.getReview().getId(),
							ReviewImage::getImageUrl,
							(existing, ignored) -> existing
						));

					// 변환
					reviews = topReviews.stream()
						.map(review -> reviewMapper.toSimpleReviewResponse(review, imageMap.get(review.getId())))
						.toList();
				}

				// 4-3. 현재 유효한 제휴 정보 조회 (단순화: 해당 장소의 유효한 제휴 아무거나 하나)
				// 필요 시 user의 학교/학과 정보를 필터링 조건에 추가 가능
				List<StudentCouncilPost> posts = studentCouncilPostRepository.findPinsInBounds(
					null, null, null, // 전체 범위
					PostCategory.PARTNERSHIP,
					CouncilType.MAJOR_COUNCIL, CouncilType.COLLEGE_COUNCIL, CouncilType.SCHOOL_COUNCIL,
					-90.0, 90.0, -180.0, 180.0, // 전체 좌표 범위
					now
				);

				// 현재 장소(placeId)와 일치하는 제휴글 필터링
				activePost = posts.stream()
					.filter(p -> p.getPlace().getPlaceId().equals(placeId))
					.findFirst()
					.orElse(null);
			}

			// 5. 거리 계산
			double distance = GeoUtil.distanceMeter(
				lat, lng,
				info.coordinate().latitude(), info.coordinate().longitude()
			);
			double roundedDistance = Math.round(distance * 100.0) / 100.0;

			// 6. 최종 매핑
			return placeMapper.toPartnershipDetailResponseFromSearch(
				info, dbPlace, isLiked, roundedDistance, avgStar, reviews, activePost
			);

		}).toList();
	}
}
