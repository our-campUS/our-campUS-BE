package com.campus.campus.domain.place.application.service;

import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campus.campus.domain.council.domain.entity.CouncilType;
import com.campus.campus.domain.councilpost.application.exception.AcademicInfoNotSetException;
import com.campus.campus.domain.councilpost.application.exception.PlaceInfoNotFoundException;
import com.campus.campus.domain.councilpost.application.exception.PostNotFoundException;
import com.campus.campus.domain.councilpost.domain.entity.PostCategory;
import com.campus.campus.domain.councilpost.domain.entity.PostImage;
import com.campus.campus.domain.councilpost.domain.entity.StudentCouncilPost;
import com.campus.campus.domain.councilpost.domain.repository.PostImageRepository;
import com.campus.campus.domain.councilpost.domain.repository.StudentCouncilPostRepository;
import com.campus.campus.domain.place.application.dto.response.PartnershipPinResponse;
import com.campus.campus.domain.place.application.dto.response.PlaceDetailView;
import com.campus.campus.domain.place.application.dto.response.partnership.PartnershipDetailResponse;
import com.campus.campus.domain.place.application.mapper.PlaceMapper;
import com.campus.campus.domain.place.domain.entity.Place;
import com.campus.campus.domain.place.domain.repository.LikedPlacesRepository;
import com.campus.campus.domain.place.domain.repository.PlaceImagesRepository;
import com.campus.campus.domain.place.domain.repository.PlaceRepository;
import com.campus.campus.domain.review.application.dto.response.SimpleReviewResponse;
import com.campus.campus.domain.review.application.service.ReviewService;
import com.campus.campus.domain.user.application.exception.UserNotFoundException;
import com.campus.campus.domain.user.domain.entity.User;
import com.campus.campus.domain.user.domain.repository.UserRepository;
import com.campus.campus.global.util.geocoder.GeoUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class PartnershipPlaceService {

	private final UserRepository userRepository;
	private final LikedPlacesRepository likedPlacesRepository;
	private final PostImageRepository postImageRepository;
	private final PlaceMapper placeMapper;
	private final StudentCouncilPostRepository studentCouncilPostRepository;
	private final ReviewService reviewService;
	private final PlaceRepository placeRepository;
	private final PlaceImagesRepository placeImagesRepository;
	private final PlaceService placeService;

	@Transactional(readOnly = true)
	public List<PartnershipDetailResponse> getPartnershipPlaces(Long userId, Long cursor, int size, double userLat,
		double userLng) {
		User user = userRepository.findById(userId)
			.orElseThrow(UserNotFoundException::new);

		validateAcademicInfo(user);

		Long majorId = user.getMajor().getMajorId();
		Long collegeId = user.getCollege().getCollegeId();
		Long schoolId = user.getSchool().getSchoolId();

		Pageable pageable = PageRequest.of(0, size);

		//유저가 속한 학생회들의 제휴글(major/college/school) 전부 조회
		List<StudentCouncilPost> posts = studentCouncilPostRepository.findByUserScopeWithCursor(
			majorId, collegeId, schoolId,
			PostCategory.PARTNERSHIP,
			CouncilType.MAJOR_COUNCIL, CouncilType.COLLEGE_COUNCIL, CouncilType.SCHOOL_COUNCIL,
			cursor, LocalDateTime.now(), pageable
		);

		List<AbstractMap.SimpleEntry<StudentCouncilPost, Double>> sortedEntries = posts.stream()
			.map(post -> {
				Place place = post.getPlace();
				double distanceMeter = GeoUtil.distanceMeter(
					userLat, userLng,
					place.getCoordinate().latitude(),
					place.getCoordinate().longitude()
				);
				return new AbstractMap.SimpleEntry<>(post, distanceMeter);
			})
			.sorted(Map.Entry.comparingByValue())
			.limit(size)
			.toList();

		if (sortedEntries.isEmpty()) {
			return List.of();
		}

		List<StudentCouncilPost> targetPosts = sortedEntries.stream()
			.map(AbstractMap.SimpleEntry::getKey)
			.toList();

		Set<Long> placeIds = targetPosts.stream()
			.map(post -> post.getPlace().getPlaceId())
			.collect(Collectors.toSet());

		Map<Long, Double> averageStarMap =
			reviewService.getAverageListOfStars(placeIds);

		Map<Long, List<String>> postImageMap = postImageRepository.findAllByPostIn(targetPosts)
			.stream()
			.collect(Collectors.groupingBy(
				img -> img.getPost().getId(),
				Collectors.mapping(PostImage::getImageUrl, Collectors.toList())
			));

		Set<Long> likedPlaceIds;
		if (placeIds.isEmpty()) {
			likedPlaceIds = Collections.emptySet();
		} else {
			likedPlaceIds = likedPlacesRepository.findLikedPlaceIds(userId, placeIds);
		}

		return sortedEntries.stream()
			.map(entry -> {
				StudentCouncilPost post = entry.getKey();
				double distanceMeter = entry.getValue();
				double rounded = Math.round(distanceMeter * 100.0) / 100.0;

				List<String> images = postImageMap.getOrDefault(post.getId(), List.of());
				boolean isLiked = likedPlaceIds.contains(post.getPlace().getPlaceId());

				double averageStar = averageStarMap.getOrDefault(post.getPlace().getPlaceId(), 0.0);

				List<SimpleReviewResponse> reviews = reviewService.getReviewSummaryList(post.getPlace().getPlaceId());

				return placeMapper.toPartnershipResponse(
					user,
					post,
					post.getPlace(),
					isLiked,
					images,
					rounded,
					averageStar,
					reviews,
					size
				);
			})
			.toList();
	}

	@Transactional
	public List<PartnershipPinResponse> findPartnerInBounds(Long userId, Double minLat, Double maxLat, Double minLng,
		Double maxLng) {
		User user = userRepository.findById(userId)
			.orElseThrow(UserNotFoundException::new);

		validateAcademicInfo(user);

		Long majorId = user.getMajor().getMajorId();
		Long collegeId = user.getCollege().getCollegeId();
		Long schoolId = user.getSchool().getSchoolId();
		List<StudentCouncilPost> posts = studentCouncilPostRepository.findPinsInBounds(
			majorId, collegeId, schoolId,
			PostCategory.PARTNERSHIP,
			CouncilType.MAJOR_COUNCIL, CouncilType.COLLEGE_COUNCIL, CouncilType.SCHOOL_COUNCIL,
			minLat, maxLat, minLng, maxLng,
			LocalDateTime.now()
		);

		// 엔티티 → 응답 DTO 변환
		return posts.stream()
			.filter(post -> post.getPlace() != null)
			.map(post -> placeMapper.toPartnershipPinResponse(post, post.getPlace()))
			.toList();
	}

	//장소 상세 조회 : 제휴
	private PartnershipDetailResponse getPartnershipDetailInternal(
		StudentCouncilPost post,
		User user,
		double userLat,
		double userLng
	) {
		Place place = post.getPlace();

		double distance = calculateDistance(place, userLat, userLng);
		double averageStar = calculateAverageStar(place);

		return placeMapper.toPartnershipResponse(
			user,
			post,
			place,
			isLiked(place, user),
			getImgUrls(post),
			distance,
			averageStar,
			reviewService.getReviewSummaryList(place.getPlaceId()),
			reviewService.getReviewCount(place.getPlaceId())
		);
	}

	//장소 상세 조회 : 제휴
	@Transactional(readOnly = true)
	public PartnershipDetailResponse getPartnershipDetail(
		Long postId,
		Long userId,
		double userLat,
		double userLng
	) {
		User user = userRepository.findById(userId)
			.orElseThrow(UserNotFoundException::new);

		StudentCouncilPost post = studentCouncilPostRepository.findById(postId)
			.orElseThrow(PostNotFoundException::new);

		Place place = post.getPlace();
		if (place == null) {
			throw new PlaceInfoNotFoundException();
		}
		double distance = calculateDistance(place, userLat, userLng);
		double averageStar = calculateAverageStar(place);

		return placeMapper.toPartnershipResponse(
			user,
			post,
			place,
			isLiked(place, user),
			getImgUrls(post),
			distance,
			averageStar,
			reviewService.getReviewSummaryList(place.getPlaceId()),
			reviewService.getReviewCount(place.getPlaceId())
		);
	}

	//장소 상세 조회: 제휴X
	private PlaceDetailView getNormalPlaceDetailInternal(
		User user,
		Place place,
		double userLat,
		double userLng
	) {
		double distance = calculateDistance(place, userLat, userLng);
		double averageStar = calculateAverageStar(place);

		return placeMapper.toPlaceDetailResponse(
			place,
			isLiked(place, user),
			getPlaceImgUrls(place),
			distance,
			averageStar,
			reviewService.getReviewSummaryList(place.getPlaceId()),
			reviewService.getReviewCount(place.getPlaceId())
		);
	}

	@Transactional(readOnly = true)
	public PlaceDetailView getPlaceDetails(
		Long userId,
		Long placeId,
		double userLat,
		double userLng
	) {
		User user = userRepository.findById(userId)
			.orElseThrow(UserNotFoundException::new);

		Place place = placeRepository.findById(placeId)
			.orElseThrow(PlaceInfoNotFoundException::new);

		List<StudentCouncilPost> activePost =
			studentCouncilPostRepository.findActiveByPlaceAndUserScope(
				place,
				LocalDateTime.now(),
				CouncilType.MAJOR_COUNCIL,
				user.getMajor().getMajorId(),
				CouncilType.COLLEGE_COUNCIL,
				user.getCollege().getCollegeId(),
				CouncilType.SCHOOL_COUNCIL,
				user.getSchool().getSchoolId(),
				PageRequest.of(0, 1)
			);

		if (activePost.isEmpty()) {
			StudentCouncilPost post = activePost.get(0);
			return getPartnershipDetailInternal(post, user, userLat, userLng);
		} else {
			return getNormalPlaceDetailInternal(user, place, userLat, userLng);
		}
	}

	private double calculateDistance(Place place, double lat, double lng) {
		double distance = GeoUtil.distanceMeter(
			lat,
			lng,
			place.getCoordinate().latitude(),
			place.getCoordinate().longitude()
		);
		return Math.round(distance * 100.0) / 100.0;
	}

	private double calculateAverageStar(Place place) {
		return reviewService.getAverageOfStars(place.getPlaceId());
	}

	private boolean isLiked(Place place, User user) {
		return likedPlacesRepository.existsByUserAndPlace(user, place);
	}

	private List<String> getImgUrls(StudentCouncilPost post) {
		return postImageRepository.findImageUrlsByPost(post);
	}

	private List<String> getPlaceImgUrls(Place place) {
		return placeImagesRepository.findImageUrlsByPlace(place);
	}

	private void validateAcademicInfo(User user) {
		if (user.getSchool() == null || user.getCollege() == null || user.getMajor() == null) {
			throw new AcademicInfoNotSetException();
		}
	}

}
