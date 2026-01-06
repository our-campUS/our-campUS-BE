package com.campus.campus.domain.place.application.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campus.campus.domain.council.domain.entity.CouncilType;
import com.campus.campus.domain.councilpost.domain.entity.PostCategory;
import com.campus.campus.domain.councilpost.domain.repository.PostImageRepository;
import com.campus.campus.domain.place.application.dto.response.LikeResponse;
import com.campus.campus.domain.place.application.dto.response.SavedPlaceInfo;
import com.campus.campus.domain.place.application.dto.response.naver.NaverSearchResponse;
import com.campus.campus.domain.place.application.dto.response.partnership.PartnershipMapResponse;
import com.campus.campus.domain.place.application.dto.response.partnership.PartnershipMapSummary;
import com.campus.campus.domain.place.application.dto.response.partnership.PartnershipPlaceSummary;
import com.campus.campus.domain.place.application.dto.response.partnership.PartnershipResponse;
import com.campus.campus.domain.place.application.dto.response.partnership.PartnershipScrollResponse;
import com.campus.campus.domain.place.application.dto.response.partnership.PostImageSummary;
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

	private final NaverMapClient naverMapClient;
	private final PlaceMapper placeMapper;
	private final PlaceRepository placeRepository;
	private final GooglePlaceClient googleClient;
	private final PlaceImagesRepository placeImagesRepository;
	private final PresignedUrlService presignedUrlService;
	private final LikedPlacesRepository likedPlacesRepository;
	private final UserRepository userRepository;
	private final PostImageRepository postImageRepository;

	public List<SavedPlaceInfo> search(String keyword) {
		//네이버에서 특정 장소 기본정보 받아오기
		NaverSearchResponse naverSearchResponse = naverMapClient.searchPlaces(keyword, 5);

		return naverSearchResponse.items().stream()
			.map(item -> {

				String name = stripHtml(item.title());
				String address = item.roadAddress();
				String placeKey = PlaceKeyGenerator.generate(name, address);
				List<String> placeImages = getPlaceImgs(placeKey, name, address);
				String naverPlaceUrl = buildNaverPlaceUrl(item);

				return placeMapper.toSavedPlaceInfo(item, name, placeKey, naverPlaceUrl, placeImages);
			})
			.toList();
	}

	@Transactional
	public Place findOrCreatePlace(String placeName) {
		NaverSearchResponse naverSearchResponse = naverMapClient.searchPlaces(placeName, 1);
		NaverSearchResponse.Item item = naverSearchResponse.items().getFirst();
		//placeKey 생성
		String placeKey = PlaceKeyGenerator.generate(item.title(), item.roadAddress());

		//이미 Place 존재하는지 확인
		Optional<Place> existing = placeRepository.findByPlaceKey(placeKey);
		if (existing.isPresent()) {
			return existing.get();
		}

		//Place 생성
		String name = stripHtml(item.title());
		String naverPlaceUrl = buildNaverPlaceUrl(item);

		SavedPlaceInfo info = placeMapper.toSavedPlaceInfo(item, name, placeKey, naverPlaceUrl, null);
		return placeMapper.createPlace(info);
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
	public PartnershipScrollResponse getPartnershipPlaces(Long userId, Long cursor, int size) {
		Pageable pageable = PageRequest.of(0, size + 1);

		User user = userRepository.findById(userId)
			.orElseThrow(UserNotFoundException::new);

		Long majorId = user.getMajor().getMajorId();
		Long collegeId = user.getCollege().getCollegeId();
		Long schoolId = user.getSchool().getSchoolId();

		//유저가 속한 과/단과대/학교 학생회에서 올린 제휴 전부 조회
		List<PartnershipPlaceSummary> response = placeRepository.findPartnershipPlaces(PostCategory.PARTNERSHIP,
			majorId, collegeId, schoolId, cursor, pageable);

		//placeId 목록 추출
		List<Long> placeIds = response.stream()
			.map(PartnershipPlaceSummary::placeId)
			.distinct().toList();

		//유저가 좋아요 한 placeId 조회
		Set<Long> likedPlaceIds =
			likedPlacesRepository.findLikedPlaceIds(userId, placeIds);

		//이미지 조회
		List<PostImageSummary> images = postImageRepository.findPartnershipImagesByPlaceIds(placeIds);

		//placeId 기준으로 이미지 묶기
		Map<Long, List<String>> imageMap =
			images.stream()
				.collect(Collectors.groupingBy(
					PostImageSummary::placeId,
					Collectors.mapping(PostImageSummary::imageUrl, Collectors.toList())
				));

		//placeId 기준으로 묶기
		Map<Long, List<PartnershipPlaceSummary>> grouped = response.stream()
			.collect(Collectors.groupingBy(PartnershipPlaceSummary::placeId));

		//다음 페이지 판단
		boolean hasNext = grouped.size() > size;

		//그룹 단위 Response 생성
		List<PartnershipResponse> items = grouped.values().stream()
			.limit(size)
			.map(group -> {
				PartnershipPlaceSummary first = group.get(0);

				List<String> tags = group.stream()
					.map(r -> resolveTag(r.councilType(), user))
					.distinct()
					.toList();

				//이미지
				List<String> imageUrls =
					imageMap.getOrDefault(first.placeId(), List.of())
						.stream()
						.toList();

				boolean isLiked = likedPlaceIds.contains(first.placeId());

				return placeMapper.toPartnershipResponse(first, tags, isLiked, imageUrls);
			})
			.toList();

		Long nextCursor = hasNext ? items.get(items.size() - 1).placeId() : null;
		return placeMapper.toPartnershipScrollResponse(items, hasNext, nextCursor);

	}

	private String resolveTag(CouncilType councilType, User user) {
		return switch (councilType) {
			case SCHOOL_COUNCIL -> "총학생회";
			case COLLEGE_COUNCIL -> user.getCollege().getCollegeName();
			case MAJOR_COUNCIL -> user.getMajor().getMajorName();
		};
	}

	public List<PartnershipMapResponse> getPartnershipPlacesForMap(
		Long userId,
		double minLat,
		double maxLat,
		double minLng,
		double maxLng
	) {
		User user = userRepository.findById(userId)
			.orElseThrow(UserNotFoundException::new);
		List<PartnershipMapSummary> rows =
			placeRepository.findPartnershipPlacesForMap(
				minLat,
				maxLat,
				minLng,
				maxLng,
				user.getMajor().getMajorId(),
				user.getCollege().getCollegeId(),
				user.getSchool().getSchoolId()
			);

		Map<Long, List<PartnershipMapSummary>> grouped =
			rows.stream()
				.collect(Collectors.groupingBy(PartnershipMapSummary::placeId));

		return grouped.values().stream()
			.map(group -> {
				PartnershipMapSummary first = group.get(0);

				List<String> tags = group.stream()
					.map(r -> resolveTag(r.councilType(), user))
					.distinct()
					.toList();

				return new PartnershipMapResponse(
					first.placeId(),
					first.latitude(),
					first.longitude(),
					tags
				);
			})
			.toList();
	}

	/**
	 * 태그 제거용
	 */
	private String stripHtml(String text) {
		return text.replaceAll("<[^>]*>", "");
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
}
