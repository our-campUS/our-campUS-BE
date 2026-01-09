package com.campus.campus.domain.partnership.application.service;

import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campus.campus.domain.council.domain.entity.CouncilType;
import com.campus.campus.domain.councilpost.application.exception.PostNotFoundException;
import com.campus.campus.domain.councilpost.domain.entity.PostCategory;
import com.campus.campus.domain.councilpost.domain.entity.StudentCouncilPost;
import com.campus.campus.domain.councilpost.domain.repository.PostImageRepository;
import com.campus.campus.domain.councilpost.domain.repository.StudentCouncilPostRepository;
import com.campus.campus.domain.partnership.application.dto.response.PartnershipPinResponse;
import com.campus.campus.domain.place.application.dto.response.partnership.PartnershipResponse;
import com.campus.campus.domain.place.application.mapper.PlaceMapper;
import com.campus.campus.domain.place.domain.entity.Place;
import com.campus.campus.domain.place.domain.repository.LikedPlacesRepository;
import com.campus.campus.domain.place.domain.repository.PlaceRepository;
import com.campus.campus.domain.user.application.exception.UserNotFoundException;
import com.campus.campus.domain.user.domain.entity.User;
import com.campus.campus.domain.user.domain.repository.UserRepository;
import com.campus.campus.global.util.jwt.GeoUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class PartnershipService {

	private final UserRepository userRepository;
	private final LikedPlacesRepository likedPlacesRepository;
	private final PostImageRepository postImageRepository;
	private final PlaceMapper placeMapper;
	private final StudentCouncilPostRepository studentCouncilPostRepository;
	private final PlaceRepository placeRepository;

	@Transactional
	public List<PartnershipResponse> getPartnershipPlaces(Long userId, Long cursor, int size, double userLat,
		double userLng) {
		User user = userRepository.findById(userId)
			.orElseThrow(UserNotFoundException::new);

		Long majorId = user.getMajor().getMajorId();
		Long collegeId = user.getCollege().getCollegeId();
		Long schoolId = user.getSchool().getSchoolId();

		Pageable pageable = PageRequest.of(0, size);

		//유저가 속한 학생회들의 제휴글(major/college/school) 전부 조회
		List<StudentCouncilPost> posts = studentCouncilPostRepository.findByUserScopeWithCursor(
			majorId,
			collegeId,
			schoolId,
			PostCategory.PARTNERSHIP,
			CouncilType.MAJOR_COUNCIL,
			CouncilType.COLLEGE_COUNCIL,
			CouncilType.SCHOOL_COUNCIL,
			cursor,
			LocalDateTime.now(),
			pageable
		);

		return posts.stream()
			.map(post -> {
				Place place = post.getPlace();

				double distanceMeter = GeoUtil.distanceMeter(
					userLat, userLng,
					place.getCoordinate().latitude(),
					place.getCoordinate().longitude()
				);

				// post + distance를 함께 묶음
				return new AbstractMap.SimpleEntry<>(post, distanceMeter);
			})
			.sorted(Map.Entry.comparingByValue()) // 거리순 정렬
			.limit(size)
			.map(entry -> {
				StudentCouncilPost post = entry.getKey();
				double distanceMeter = entry.getValue();
				double rounded = Math.round(distanceMeter * 100.0) / 100.0;

				return placeMapper.toPartnershipResponse(
					user,
					post,
					post.getPlace(),
					isLiked(post.getPlace(), user),
					getImgUrls(post),
					rounded
				);
			})
			.toList();
	}

	@Transactional
	public List<PartnershipPinResponse> findPartnerInBounds(Long userId, Double minLat, Double maxLat, Double minLng,
		Double maxLng) {
		User user = userRepository.findById(userId)
			.orElseThrow(UserNotFoundException::new);

		Long majorId = user.getMajor().getMajorId();
		Long collegeId = user.getCollege().getCollegeId();
		Long schoolId = user.getSchool().getSchoolId();
		List<StudentCouncilPost> posts = studentCouncilPostRepository.findPinsInBounds(
			majorId,
			collegeId,
			schoolId,
			PostCategory.PARTNERSHIP,
			CouncilType.MAJOR_COUNCIL,
			CouncilType.COLLEGE_COUNCIL,
			CouncilType.SCHOOL_COUNCIL,
			minLat,
			maxLat,
			minLng,
			maxLng,
			LocalDateTime.now()
		);

		// 엔티티 → 응답 DTO 변환
		return posts.stream()
			.map(post -> placeMapper.toPartnershipPinResponse(post, post.getPlace()))
			.toList();
	}

	@Transactional
	public PartnershipResponse getPartnershipDetail(Long postId, Long userId, double userLat,
		double userLng) {
		StudentCouncilPost post = studentCouncilPostRepository.findById(postId)
			.orElseThrow(PostNotFoundException::new);
		Place place = post.getPlace();

		User user = userRepository.findById(userId)
			.orElseThrow(UserNotFoundException::new);

		double distanceMeter = GeoUtil.distanceMeter(
			userLat, userLng,
			place.getCoordinate().latitude(),
			place.getCoordinate().longitude()
		);
		double rounded = Math.round(distanceMeter * 100.0) / 100.0;

		return placeMapper.toPartnershipResponse(user, post, place, isLiked(place, user), getImgUrls(post), rounded);
	}

	private boolean isLiked(Place place, User user) {
		return likedPlacesRepository.existsByUserAndPlace(user, place);
	}

	private List<String> getImgUrls(StudentCouncilPost post) {
		return postImageRepository.findImageUrlsByPost(post);
	}

}
