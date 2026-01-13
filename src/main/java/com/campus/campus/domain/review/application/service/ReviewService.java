package com.campus.campus.domain.review.application.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campus.campus.domain.councilpost.application.exception.PostImageLimitExceededException;
import com.campus.campus.domain.councilpost.domain.entity.StudentCouncilPost;
import com.campus.campus.domain.councilpost.domain.repository.StudentCouncilPostRepository;
import com.campus.campus.domain.place.application.service.PlaceService;
import com.campus.campus.domain.place.domain.entity.Place;
import com.campus.campus.domain.review.application.dto.request.ReviewRequest;
import com.campus.campus.domain.review.application.dto.response.CursorPageReviewResponse;
import com.campus.campus.domain.review.application.dto.response.PlaceReviewRankResponse;
import com.campus.campus.domain.review.application.dto.response.ReviewCreateResponse;
import com.campus.campus.domain.review.application.dto.response.ReviewCreateResult;
import com.campus.campus.domain.review.application.dto.response.ReviewRankingResponse;
import com.campus.campus.domain.review.application.dto.response.ReviewResponse;
import com.campus.campus.domain.review.application.dto.response.WriteReviewResponse;
import com.campus.campus.domain.review.application.exception.NotUserWriterException;
import com.campus.campus.domain.review.application.exception.ReviewNotFoundException;
import com.campus.campus.domain.review.application.mapper.ReviewMapper;
import com.campus.campus.domain.review.domain.entity.Review;
import com.campus.campus.domain.review.domain.entity.ReviewImage;
import com.campus.campus.domain.review.domain.repository.ReviewImageRepository;
import com.campus.campus.domain.review.domain.repository.ReviewRepository;
import com.campus.campus.domain.school.domain.entity.College;
import com.campus.campus.domain.school.domain.entity.Major;
import com.campus.campus.domain.school.domain.entity.School;
import com.campus.campus.domain.user.application.exception.UserNotFoundException;
import com.campus.campus.domain.user.domain.entity.User;
import com.campus.campus.domain.user.domain.repository.UserRepository;
import com.campus.campus.global.oci.application.service.PresignedUrlService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {

	private final UserRepository userRepository;
	private final ReviewMapper reviewMapper;
	private final ReviewRepository reviewRepository;
	private final PlaceService placeService;
	private final ReviewImageRepository reviewImageRepository;
	private final PresignedUrlService presignedUrlService;
	private final StudentCouncilPostRepository studentCouncilPostRepository;

	@Transactional
	public ReviewCreateResponse writeReview(ReviewRequest request, Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(UserNotFoundException::new);

		if (request.imageUrls() != null && request.imageUrls().size() > 10) {
			throw new PostImageLimitExceededException();
		}

		//place 객체 생성
		Place place = placeService.findOrCreatePlace(request.place());

		Review review = reviewMapper.createReview(request, user, place);
		reviewRepository.save(review);

		if (request.imageUrls() != null) {
			for (String imageUrl : request.imageUrls()) {
				reviewImageRepository.save(reviewMapper.createReviewImage(review, imageUrl));
			}
		}

		String imageUrl =
			(request.imageUrls() == null || request.imageUrls().isEmpty())
				? null : request.imageUrls().getFirst();

		WriteReviewResponse response = reviewMapper.toWriteReviewResponse(review, imageUrl);
		ReviewCreateResult createResult = getCreateResult(place, user);
		ReviewRankingResponse rankingResponse = getRankingResult(place, user);

		return reviewMapper.toReviewCreateResponse(response, createResult, rankingResponse);
	}

	@Transactional(readOnly = true)
	public ReviewResponse readReview(Long reviewId) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(ReviewNotFoundException::new);

		List<String> imageUrls = reviewImageRepository
			.findAllByReviewOrderByIdAsc(review)
			.stream()
			.map(ReviewImage::getImageUrl)
			.toList();

		return reviewMapper.toReviewResponse(review, imageUrls);
	}

	@Transactional
	public void delete(Long userId, Long reviewId) {

		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(ReviewNotFoundException::new);

		if (!review.getUser().getId().equals(userId)) {
			throw new NotUserWriterException();
		}

		List<ReviewImage> reviewImages = reviewImageRepository.findAllByReview(review);

		Set<String> deleted = new HashSet<>();
		reviewImages.stream()
			.map(ReviewImage::getImageUrl)
			.forEach(deleted::add);

		reviewImageRepository.deleteAll(reviewImages);
		reviewRepository.delete(review);

		for (String imageUrl : deleted) {
			try {
				presignedUrlService.deleteImage(imageUrl);
			} catch (Exception e) {
				log.warn("OCI 파일 삭제 실패: {}", imageUrl, e);
			}
		}
	}

	@Transactional
	public WriteReviewResponse update(Long userId, Long reviewId, ReviewRequest request) {

		if (request.imageUrls() != null && request.imageUrls().size() > 10) {
			throw new PostImageLimitExceededException();
		}

		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(ReviewNotFoundException::new);

		if (!review.getUser().getId().equals(userId)) {
			throw new NotUserWriterException();
		}

		List<ReviewImage> oldImages = reviewImageRepository.findAllByReview(review);
		review.update(
			request.content(),
			request.star()
		);

		reviewImageRepository.deleteByReview(review);
		if (request.imageUrls() != null) {
			for (String imageUrl : request.imageUrls()) {
				reviewImageRepository.save(reviewMapper.createReviewImage(review, imageUrl));
			}
		}

		cleanupUnusedImages(oldImages, request);

		String imageUrl =
			(request.imageUrls() == null || request.imageUrls().isEmpty())
				? null : request.imageUrls().getFirst();

		return reviewMapper.toWriteReviewResponse(review, imageUrl);
	}

	@Transactional(readOnly = true)
	public CursorPageReviewResponse<ReviewResponse> getReviewList(
		Long placeId,
		LocalDateTime cursorCreatedAt,
		Long cursorId,
		int size
	) {

		//size+1로 조회 -> 다음 페이지 여부(hasNext) 판단
		Pageable pageable = PageRequest.of(0, size + 1);
		List<Review> fetched = reviewRepository.findByPlaceIdWithCursor(
			placeId, cursorCreatedAt, cursorId, pageable
		);

		//다음 페이지가 있는지 판단, 실제로 내려줄 items는 size개만 자름
		boolean hasNext = fetched.size() > size;
		List<Review> reviews = hasNext ? fetched.subList(0, size) : fetched;

		if (reviews.isEmpty()) {
			return reviewMapper.toEmptyCursorReviewResponse();
		}

		//리뷰 ID를 뽑아서 이미지들을 한 번에 조회
		List<Long> reviewIds = reviews.stream()
			.map(Review::getId)
			.toList();

		//reviewId -> imageUrls로 그룹핑
		Map<Long, List<String>> imageMap = reviewImageRepository
			.findAllByReviewIdInOrderByIdAsc(reviewIds)
			.stream()
			.collect(Collectors.groupingBy(
				ri -> ri.getReview().getId(),
				Collectors.mapping(ReviewImage::getImageUrl, Collectors.toList())
			));

		List<ReviewResponse> items = reviews.stream()
			.map(review ->
				reviewMapper.toReviewResponse(
					review,
					imageMap.get(review.getId())
				)
			)
			.toList();

		Review last = reviews.getLast();

		return reviewMapper.toCursorReviewResponse(items, last, hasNext);

	}

	@Transactional(readOnly = true)
	public List<PlaceReviewRankResponse> readPopularPartnerships(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(UserNotFoundException::new);

		LocalDateTime now = LocalDateTime.now();
		LocalDateTime from = now.minusMonths(1);

		//리뷰가 가장 많은 top3 placeIds
		List<StudentCouncilPost> partnerships =
			studentCouncilPostRepository.findTop3RecommendedPartnershipPlaces(
				user.getMajor().getMajorId(),
				user.getCollege().getCollegeId(),
				user.getSchool().getSchoolId(),
				from,
				now,
				PageRequest.of(0, 3)
			);

		log.info("찾은 결과:{}", partnerships.stream().toList());

		if (partnerships.isEmpty()) {
			return List.of();
		}

		return partnerships.stream()
			.map(reviewMapper::toTopPartnershipResponse)
			.toList();
	}

	//이미지 삭제
	private void cleanupUnusedImages(List<ReviewImage> oldImages, ReviewRequest request) {
		List<String> newUrls = request.imageUrls() == null ? List.of() : request.imageUrls();
		Set<String> deleteTargets = new HashSet<>();

		// 본문 이미지 중 제거된 이미지
		oldImages.stream()
			.map(ReviewImage::getImageUrl)
			.filter(url -> !newUrls.contains(url))
			.forEach(deleteTargets::add);

		//삭제
		for (String imageUrl : deleteTargets) {
			if (imageUrl == null || imageUrl.isBlank()) {
				continue;
			}

			try {
				presignedUrlService.deleteImage(imageUrl);
			} catch (Exception e) {
				log.warn("OCI 파일 삭제 실패 (파일이 없을 수 있음): {}", imageUrl, e);
			}
		}
	}

	private ReviewCreateResult getCreateResult(Place place, User user) {
		//해당 장소 리뷰 개수
		long totalReviewCountOfPlace = reviewRepository.countByPlace_PlaceId(place.getPlaceId());

		//해당 장소에서 유저가 쓴 리뷰가 몇번째인지
		long count = reviewRepository.countByPlaceAndUser(place, user);
		boolean isFirstReviewOfPlace = totalReviewCountOfPlace == 1;

		return reviewMapper.toReviewCreateResult(isFirstReviewOfPlace, count);

	}

	private ReviewRankingResponse getRankingResult(Place place, User user) {
		Long placeId = place.getPlaceId();
		Major major = user.getMajor();
		College college = user.getCollege();
		School school = user.getSchool();

		Long majorId = major.getMajorId();
		Long collegeId = college.getCollegeId();
		Long schoolId = school.getSchoolId();

		long majorRank = reviewRepository.countByPlace_PlaceIdAndUser_Major_MajorId(
			placeId,
			majorId
		);

		long collegeRank = reviewRepository.countByPlace_PlaceIdAndUser_College_CollegeId(
			placeId,
			collegeId
		);

		long schoolRank = reviewRepository.countByPlace_PlaceIdAndUser_School_SchoolId(
			placeId,
			schoolId
		);

		return reviewMapper.toReviewRankingResponse(major.getMajorName(), majorRank, college.getCollegeName(),
			collegeRank,
			school.getSchoolName(), schoolRank);
	}

}
