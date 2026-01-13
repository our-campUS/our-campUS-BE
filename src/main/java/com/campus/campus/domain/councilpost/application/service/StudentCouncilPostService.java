package com.campus.campus.domain.councilpost.application.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campus.campus.domain.council.application.exception.StudentCouncilNotFoundException;
import com.campus.campus.domain.council.domain.entity.StudentCouncil;
import com.campus.campus.domain.council.domain.repository.StudentCouncilRepository;
import com.campus.campus.domain.councilpost.application.dto.request.PostRequest;
import com.campus.campus.domain.councilpost.application.dto.response.GetPostDetailResponse;
import com.campus.campus.domain.councilpost.application.dto.response.GetPostListForCouncilResponse;
import com.campus.campus.domain.councilpost.application.dto.response.GetPostResponse;
import com.campus.campus.domain.councilpost.application.dto.response.GetUpcomingEventListForCouncilResponse;
import com.campus.campus.domain.councilpost.application.dto.response.NormalizedDateTime;
import com.campus.campus.domain.councilpost.application.exception.NotPostWriterException;
import com.campus.campus.domain.councilpost.application.exception.PostImageLimitExceededException;
import com.campus.campus.domain.councilpost.application.exception.PostNotFoundException;
import com.campus.campus.domain.councilpost.application.exception.ThumbnailRequiredException;
import com.campus.campus.domain.councilpost.application.mapper.StudentCouncilPostMapper;
import com.campus.campus.domain.councilpost.domain.entity.PostCategory;
import com.campus.campus.domain.councilpost.domain.entity.PostImage;
import com.campus.campus.domain.councilpost.domain.entity.StudentCouncilPost;
import com.campus.campus.domain.councilpost.domain.repository.PostImageRepository;
import com.campus.campus.domain.councilpost.domain.repository.StudentCouncilPostRepository;
import com.campus.campus.domain.place.application.service.PlaceService;
import com.campus.campus.domain.place.domain.entity.Place;
import com.campus.campus.domain.place.domain.entity.PlaceImages;
import com.campus.campus.domain.place.domain.repository.PlaceImagesRepository;
import com.campus.campus.global.oci.application.service.PresignedUrlService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentCouncilPostService {

	private static final int MAX_IMAGE_COUNT = 10;
	private static final long UPCOMING_EVENT_WINDOW_HOURS = 72L;
	private final StudentCouncilPostRepository postRepository;
	private final StudentCouncilRepository studentCouncilRepository;
	private final PostImageRepository postImageRepository;
	private final PlaceImagesRepository placeImagesRepository;
	private final PresignedUrlService presignedUrlService;
	private final StudentCouncilPostMapper studentCouncilPostMapper;
	private final ApplicationEventPublisher eventPublisher;
	private final PlaceService placeService;

	@Transactional
	public GetPostResponse create(Long councilId, PostRequest dto) {
		if (dto.imageUrls() != null && dto.imageUrls().size() > MAX_IMAGE_COUNT) {
			throw new PostImageLimitExceededException();
		}

		StudentCouncil writer = studentCouncilRepository
			.findByIdWithDetailsAndManagerApprovedIsTrueAndDeletedAtIsNull(councilId)
			.orElseThrow(StudentCouncilNotFoundException::new);

		if (dto.thumbnailImageUrl() == null && dto.thumbnailIcon() == null) {
			throw new ThumbnailRequiredException();
		}

		NormalizedDateTime normalized = dto.category().validateAndNormalize(dto);

		//Place 객체 생성
		Place place = placeService.findOrCreatePlace(dto.place());

		StudentCouncilPost post = studentCouncilPostMapper.createStudentCouncilPost(
			writer, place, dto, normalized.startDateTime(), normalized.endDateTime()
		);

		StudentCouncilPost saved = postRepository.save(post);

		if (dto.imageUrls() != null) {
			for (String imageUrl : dto.imageUrls()) {
				postImageRepository.save(studentCouncilPostMapper.createPostImage(post, imageUrl));
			}
		}

		eventPublisher.publishEvent(studentCouncilPostMapper.createPostCreatedEvent(saved, writer));

		List<String> imageUrls = postImageRepository
			.findAllByPostOrderByIdAsc(post)
			.stream()
			.map(PostImage::getImageUrl)
			.toList();

		return studentCouncilPostMapper.toGetPostResponse(post, imageUrls, councilId);
	}

	@Transactional(readOnly = true)
	public GetPostDetailResponse findById(Long postId, Long currentCouncilId) {
		StudentCouncilPost post = postRepository.findByIdWithFullInfo(postId)
			.orElseThrow(PostNotFoundException::new);

		List<String> imageUrls = postImageRepository
			.findAllByPostOrderByIdAsc(post)
			.stream()
			.map(PostImage::getImageUrl)
			.toList();

		List<String> placeImageUrls = getPlaceImageUrls(post.getPlace());

		return studentCouncilPostMapper.toGetPostDetailResponse(post, imageUrls, placeImageUrls, currentCouncilId);
	}

	@Transactional(readOnly = true)
	public Page<GetPostListForCouncilResponse> findPostListForCouncil(Long councilId, PostCategory category,
		int page, int size) {
		studentCouncilRepository.findByIdAndManagerApprovedIsTrueAndDeletedAtIsNull(councilId)
			.orElseThrow(StudentCouncilNotFoundException::new);

		Sort sort;
		if (category == PostCategory.EVENT) {
			sort = Sort.by(Sort.Direction.ASC, "startDateTime");
		} else {
			sort = Sort.by(Sort.Direction.ASC, "endDateTime");
		}

		Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size, sort);

		LocalDateTime now = LocalDateTime.now();

		Page<StudentCouncilPost> posts = postRepository.findPostsByCouncilAndFilters(councilId, category, now,
			pageable);

		return posts.map(studentCouncilPostMapper::toGetPostListForCouncilResponse);
	}

	@Transactional(readOnly = true)
	public Page<GetUpcomingEventListForCouncilResponse> findUpcomingEventsForCouncil(Long councilId,
		int page, int size) {
		Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size,
			Sort.by(Sort.Direction.ASC, "startDateTime"));

		studentCouncilRepository.findByIdAndManagerApprovedIsTrueAndDeletedAtIsNull(councilId)
			.orElseThrow(StudentCouncilNotFoundException::new);

		LocalDateTime now = LocalDateTime.now();
		LocalDateTime limit = now.plusHours(UPCOMING_EVENT_WINDOW_HOURS);

		Page<StudentCouncilPost> posts = postRepository.findUpcomingEventsByCouncil(councilId, PostCategory.EVENT,
			now, limit, pageable);

		return posts.map(studentCouncilPostMapper::toGetUpcomingEventListForCouncilResponse);
	}

	@Transactional
	public void delete(Long councilId, Long postId) {
		studentCouncilRepository.findByIdAndManagerApprovedIsTrueAndDeletedAtIsNull(councilId)
			.orElseThrow(StudentCouncilNotFoundException::new);

		StudentCouncilPost post = postRepository.findByIdWithFullInfo(postId)
			.orElseThrow(PostNotFoundException::new);

		if (!post.getWriter().getId().equals(councilId)) {
			throw new NotPostWriterException();
		}

		List<PostImage> postImages = postImageRepository.findAllByPost(post);

		Set<String> deleteTargets = new HashSet<>();

		if (post.getThumbnailImageUrl() != null) {
			deleteTargets.add(post.getThumbnailImageUrl());
		}

		postImages.stream()
			.map(PostImage::getImageUrl)
			.forEach(deleteTargets::add);

		postImageRepository.deleteAll(postImages);
		postRepository.delete(post);

		for (String imageUrl : deleteTargets) {
			try {
				presignedUrlService.deleteImage(imageUrl);
			} catch (Exception e) {
				log.warn("OCI 파일 삭제 실패: {}", imageUrl, e);
			}
		}
	}

	@Transactional
	public GetPostResponse update(Long councilId, Long postId, PostRequest dto) {
		studentCouncilRepository.findByIdAndManagerApprovedIsTrueAndDeletedAtIsNull(councilId)
			.orElseThrow(StudentCouncilNotFoundException::new);

		if (dto.imageUrls() != null && dto.imageUrls().size() > 10) {
			throw new PostImageLimitExceededException();
		}

		StudentCouncilPost post = postRepository.findByIdWithFullInfo(postId)
			.orElseThrow(PostNotFoundException::new);

		if (!post.getWriter().getId().equals(councilId)) {
			throw new NotPostWriterException();
		}

		// 썸네일 검증
		if (dto.thumbnailImageUrl() == null && dto.thumbnailIcon() == null) {
			throw new ThumbnailRequiredException();
		}

		NormalizedDateTime normalized = dto.category().validateAndNormalize(dto);

		String oldThumbnailUrl = post.getThumbnailImageUrl();
		List<PostImage> oldImages = postImageRepository.findAllByPost(post);

		Place place = post.getPlace();
		if (dto.place() != null && (place == null || !dto.place().placeName().equals(place.getPlaceName()))) {
			place = placeService.findOrCreatePlace(dto.place());
		}

		post.update(
			dto.title(),
			dto.content(),
			place,
			dto.detailedLocation(),
			normalized.startDateTime(),
			normalized.endDateTime(),
			dto.thumbnailImageUrl(),
			dto.thumbnailIcon(),
			dto.category()
		);

		postImageRepository.deleteByPost(post);

		if (dto.imageUrls() != null) {
			for (String imageUrl : dto.imageUrls()) {
				postImageRepository.save(studentCouncilPostMapper.createPostImage(post, imageUrl));
			}
		}

		cleanupUnusedImages(oldThumbnailUrl, oldImages, dto);

		List<String> imageUrls = postImageRepository
			.findAllByPostOrderByIdAsc(post)
			.stream()
			.map(PostImage::getImageUrl)
			.toList();

		return studentCouncilPostMapper.toGetPostResponse(post, imageUrls, councilId);
	}

	private List<String> getPlaceImageUrls(Place place) {
		if (place == null || place.getPlaceKey() == null) {
			return Collections.emptyList();
		}

		return placeImagesRepository.findByPlaceKey(place.getPlaceKey()).stream()
			.map(PlaceImages::getImageUrl)
			.toList();
	}

	//이미지 삭제
	private void cleanupUnusedImages(String oldThumbnailUrl, List<PostImage> oldImages, PostRequest dto) {
		List<String> newUrls = dto.imageUrls() == null ? List.of() : dto.imageUrls();

		Set<String> deleteTargets = new HashSet<>();

		// 썸네일 변경 시 이전 썸네일
		if (oldThumbnailUrl != null && !oldThumbnailUrl.equals(dto.thumbnailImageUrl())) {
			deleteTargets.add(oldThumbnailUrl);
		}

		// 본문 이미지 중 제거된 이미지
		oldImages.stream()
			.map(PostImage::getImageUrl)
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
}
