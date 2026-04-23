package com.campus.campus.domain.review.application.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campus.campus.domain.council.domain.entity.CouncilType;
import com.campus.campus.domain.councilpost.application.exception.PlaceInfoNotFoundException;
import com.campus.campus.domain.councilpost.application.exception.PostImageLimitExceededException;
import com.campus.campus.domain.councilpost.domain.entity.StudentCouncilPost;
import com.campus.campus.domain.councilpost.domain.repository.StudentCouncilPostRepository;
import com.campus.campus.domain.place.application.exception.PlaceCreationException;
import com.campus.campus.domain.place.application.mapper.PlaceMapper;
import com.campus.campus.domain.place.domain.entity.Place;
import com.campus.campus.domain.place.domain.repository.LikedPlacesRepository;
import com.campus.campus.domain.place.domain.repository.PlaceRepository;
import com.campus.campus.domain.review.application.dto.request.PartnershipReviewRequest;
import com.campus.campus.domain.review.application.dto.request.PlaceReviewRequest;
import com.campus.campus.domain.review.application.dto.response.CursorPageReviewResponse;
import com.campus.campus.domain.review.application.dto.response.MyReviewResponse;
import com.campus.campus.domain.review.application.dto.response.PlaceReviewRankResponse;
import com.campus.campus.domain.review.application.dto.response.PlaceStarAvgRow;
import com.campus.campus.domain.review.application.dto.response.ReviewCreateResponse;
import com.campus.campus.domain.review.application.dto.response.ReviewCreateResult;
import com.campus.campus.domain.review.application.dto.response.ReviewPartnerResponse;
import com.campus.campus.domain.review.application.dto.response.ReviewRankingResponse;
import com.campus.campus.domain.review.application.dto.response.ReviewResponse;
import com.campus.campus.domain.review.application.dto.response.SimpleReviewResponse;
import com.campus.campus.domain.review.application.dto.response.WriteReviewResponse;
import com.campus.campus.domain.review.application.dto.response.ocr.ReceiptResultDto;
import com.campus.campus.domain.review.application.exception.NotPartnershipReceiptException;
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
import com.campus.campus.domain.stamp.application.service.StampService;
import com.campus.campus.domain.stamp.domain.repository.StampRepository;
import com.campus.campus.domain.user.application.exception.UserNotFoundException;
import com.campus.campus.domain.user.domain.entity.User;
import com.campus.campus.domain.user.domain.repository.UserRepository;
import com.campus.campus.global.oci.application.service.PresignedUrlService;
import com.campus.campus.global.util.geocoder.GeoUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {

	private final UserRepository userRepository;
	private final ReviewMapper reviewMapper;
	private final ReviewRepository reviewRepository;
	private final ReviewImageRepository reviewImageRepository;
	private final PresignedUrlService presignedUrlService;
	private final StudentCouncilPostRepository studentCouncilPostRepository;
	private final StampService stampService;
	private final StampRepository stampRepository;
	private final PlaceMapper placeMapper;
	private final LikedPlacesRepository likedPlacesRepository;
	private final PlaceRepository placeRepository;

	@Transactional
	public ReviewCreateResponse writePlaceReview(PlaceReviewRequest request, Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(UserNotFoundException::new);

		if (request.imageUrls() != null && request.imageUrls().size() > 10) {
			throw new PostImageLimitExceededException();
		}

		Place place = placeRepository.findByPlaceKey(request.place().placeKey())
			.orElseGet(() -> {
				try {
					return placeRepository.save(placeMapper.createPlace(request.place()));
				} catch (DataIntegrityViolationException e) {
					log.info("리뷰 작성 중 장소 동시 생성 감지: {}", request.place().placeKey());
					return placeRepository.findByPlaceKey(request.place().placeKey())
						.orElseThrow(PlaceCreationException::new);
				}
			});

		Review review = reviewMapper.createPlaceReview(request, user, place);
		reviewRepository.save(review);

		if (request.imageUrls() != null) {
			for (String imageUrl : request.imageUrls()) {
				reviewImageRepository.save(reviewMapper.createReviewImage(review, imageUrl));
			}
		}

		return createReviewResponse(review, place, user, request.imageUrls());
	}

	@Transactional
	public ReviewCreateResponse writePartnershipReview(PartnershipReviewRequest request, Long userId, Long placeId) {
		User user = userRepository.findById(userId)
			.orElseThrow(UserNotFoundException::new);

		if (request.imageUrls() != null && request.imageUrls().size() > 10) {
			throw new PostImageLimitExceededException();
		}

		Place place = placeRepository.findById(placeId)
			.orElseThrow(PlaceInfoNotFoundException::new);

		Review review = reviewMapper.createPartnershipReview(request, user, place);
		reviewRepository.save(review);

		if (request.imageUrls() != null) {
			for (String imageUrl : request.imageUrls()) {
				reviewImageRepository.save(reviewMapper.createReviewImage(review, imageUrl));
			}
		}

		if (request.isVerified()) {
			review.verify();
			stampService.grantStampForReview(user, review);
		}

		return createReviewResponse(review, place, user, request.imageUrls());
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
	public WriteReviewResponse update(Long userId, Long reviewId, PlaceReviewRequest request) {

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
	public List<SimpleReviewResponse> getReviewSummaryList(Long placeId) {

		List<Review> reviews =
			reviewRepository.findTop3ByPlace_PlaceIdOrderByCreatedAtDesc(placeId);

		if (reviews.isEmpty()) {
			return List.of();
		}

		List<Long> reviewIds = reviews.stream()
			.map(Review::getId)
			.toList();

		Map<Long, String> imageMap =
			reviewImageRepository.findAllByReviewIdInOrderByIdAsc(reviewIds)
				.stream()
				.collect(Collectors.toMap(
					img -> img.getReview().getId(),
					ReviewImage::getImageUrl,
					(existing, ignored) -> existing
				));

		return reviews.stream()
			.map(review ->
				reviewMapper.toSimpleReviewResponse(
					review,
					imageMap.get(review.getId())
				)
			)
			.toList();
	}

	@Transactional(readOnly = true)
	public CursorPageReviewResponse<ReviewResponse> getReviewList(
		Long placeId,
		String sortType,
		Integer cursorStar,
		LocalDateTime cursorCreatedAt,
		Long cursorId,
		int size
	) {

		Pageable pageable = PageRequest.of(0, size + 1);
		List<Review> fetched;

		if ("STAR".equalsIgnoreCase(sortType)) {
			fetched = reviewRepository.findByPlaceIdWithStarCursor(
				placeId,
				cursorStar,
				cursorCreatedAt,
				cursorId,
				pageable
			);
		} else {
			fetched = reviewRepository.findByPlaceIdWithLatestCursor(
				placeId,
				cursorCreatedAt,
				cursorId,
				pageable
			);
		}

		boolean hasNext = fetched.size() > size;
		List<Review> reviews = hasNext ? fetched.subList(0, size) : fetched;

		if (reviews.isEmpty()) {
			return reviewMapper.toEmptyCursorReviewResponse();
		}

		List<Long> reviewIds = reviews.stream()
			.map(Review::getId)
			.toList();

		Map<Long, List<String>> imageMap = reviewImageRepository
			.findAllByReviewIdInOrderByIdAsc(reviewIds)
			.stream()
			.collect(Collectors.groupingBy(
				ri -> ri.getReview().getId(),
				Collectors.mapping(ReviewImage::getImageUrl, Collectors.toList())
			));

		List<ReviewResponse> items = reviews.stream()
			.map(review -> reviewMapper.toReviewResponse(
				review,
				imageMap.get(review.getId())
			))
			.toList();

		Review last = reviews.getLast();

		return reviewMapper.toCursorReviewResponse(items, last, hasNext);
	}
	@Transactional(readOnly = true)
	public ReviewPartnerResponse findPartnership(Long placeId, ReceiptResultDto result, Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(UserNotFoundException::new);
		Place place = placeRepository.findById(placeId)
			.orElseThrow(PlaceInfoNotFoundException::new);

		Long majorId = user.getMajor().getMajorId();
		Long collegeId = user.getCollege().getCollegeId();
		Long schoolId = user.getSchool().getSchoolId();

		LocalDate paymentDate = result.paymentDate();
		LocalDateTime time = paymentDate.atStartOfDay(); //시간은 우선 임의로

		//제휴기간 내에 결제 했는지 확인
		StudentCouncilPost post = studentCouncilPostRepository.findValidPartnershipForUserScope(
			placeId, time, majorId, collegeId, schoolId, CouncilType.MAJOR_COUNCIL,
			CouncilType.COLLEGE_COUNCIL, CouncilType.SCHOOL_COUNCIL
		).orElseThrow(NotPartnershipReceiptException::new);

		double rawStar = getAverageOfStars(placeId);
		double averageStar = Math.round(rawStar * 10.0) / 10.0;

		String writer = post.getWriter().getCouncilName();

		boolean isLiked = likedPlacesRepository.existsByUserAndPlace(user, place);
		return placeMapper.toReviewPartnerResponse(post, post.getPlace(), averageStar, writer, isLiked, paymentDate);
	}

	public double getAverageOfStars(Long placeId) {
		return reviewRepository.findAverageStarByPlaceId(placeId).orElse(0.0);
	}

	@Transactional(readOnly = true)
	public int getReviewCount(Long placeId) {
		return (int)reviewRepository.countByPlace_PlaceId(placeId);
	}

	@Transactional(readOnly = true)
	public Map<Long, Double> getAverageListOfStars(Set<Long> placeIds) {

		if (placeIds == null || placeIds.isEmpty()) {
			return Collections.emptyMap();
		}

		List<PlaceStarAvgRow> rows =
			reviewRepository.findAverageStarsByPlaceIds(placeIds);

		// 조회된 placeId → 평균
		Map<Long, Double> avgMap = rows.stream()
			.collect(Collectors.toMap(
				PlaceStarAvgRow::placeId,
				row -> {
					double val = row.avgStar() != null ? row.avgStar() : 0.0;
					return BigDecimal.valueOf(val)
						.setScale(1, RoundingMode.HALF_UP) // 소수점 첫째 자리까지 반올림
						.doubleValue();
				}
			));

		// 리뷰가 하나도 없는 placeId는 0.0으로 채움
		for (Long placeId : placeIds) {
			avgMap.putIfAbsent(placeId, 0.0);
		}

		return avgMap;
	}

	@Transactional(readOnly = true)
	public List<PlaceReviewRankResponse> readPopularPartnerships(Long userId, double lat, double lng) {
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
			.map(post -> {
				double distance = GeoUtil.distanceMeter(
					lat, lng,
					post.getPlace().getCoordinate().latitude(),
					post.getPlace().getCoordinate().longitude()
				);
				double roundedDistance = Math.round(distance * 100.0) / 100.0;

				return reviewMapper.toTopPartnershipResponse(post, roundedDistance);
			})
			.toList();
	}

	@Transactional(readOnly = true)
	public Page<MyReviewResponse> findMyReviews(Long userId, int page, int size) {
		userRepository.findByIdAndDeletedAtIsNull(userId)
			.orElseThrow(UserNotFoundException::new);

		Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size);

		Page<Review> reviewPage = reviewRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
		List<Review> reviews = reviewPage.getContent();

		if (reviews.isEmpty()) {
			return Page.empty(pageable);
		}

		List<Long> reviewIds = reviews.stream()
			.map(Review::getId)
			.toList();

		Map<Long, List<String>> imageMap = reviewImageRepository
			.findAllByReviewIdInOrderByIdAsc(reviewIds)
			.stream()
			.collect(Collectors.groupingBy(
				ri -> ri.getReview().getId(),
				Collectors.mapping(ReviewImage::getImageUrl, Collectors.toList())
			));

		return reviewPage.map(review ->
			reviewMapper.toMyReviewResponse(review, imageMap.get(review.getId()))
		);
	}

	@Transactional(readOnly = true)
	public Map<Long, Long> getReviewCounts(Set<Long> placeIds) {
		if (placeIds == null || placeIds.isEmpty()) {
			return Collections.emptyMap();
		}

		List<Object[]> results = reviewRepository.findReviewCountsByPlaceIds(placeIds);

		Map<Long, Long> countMap = results.stream()
			.collect(Collectors.toMap(
				res -> (Long)res[0],
				res -> (Long)res[1]
			));

		for (Long placeId : placeIds) {
			countMap.putIfAbsent(placeId, 0L);
		}

		return countMap;
	}

	private ReviewCreateResponse createReviewResponse(Review review, Place place, User user, List<String> imageUrls) {
		String mainImageUrl = (imageUrls == null || imageUrls.isEmpty())
			? null : imageUrls.getFirst();

		WriteReviewResponse response = reviewMapper.toWriteReviewResponse(review, mainImageUrl);
		ReviewCreateResult createResult = getCreateResult(place, user);
		ReviewRankingResponse rankingResponse = getRankingResult(place, user);

		return reviewMapper.toReviewCreateResponse(response, createResult, rankingResponse);
	}

	//이미지 삭제
	private void cleanupUnusedImages(List<ReviewImage> oldImages, PlaceReviewRequest request) {
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
		long totalReviewCountOfPlace = reviewRepository.countByPlace_PlaceId(place.getPlaceId());

		long count = reviewRepository.countByPlaceAndUser(place, user);
		boolean isFirstReviewOfPlace = totalReviewCountOfPlace == 1;
		int NumberOfStamp = stampRepository.countByUser(user);

		return reviewMapper.toReviewCreateResult(isFirstReviewOfPlace, count, NumberOfStamp);

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
