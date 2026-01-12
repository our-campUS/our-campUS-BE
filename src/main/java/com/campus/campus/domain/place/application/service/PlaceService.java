package com.campus.campus.domain.place.application.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campus.campus.domain.councilpost.application.dto.request.PostRequest;
import com.campus.campus.domain.place.application.dto.response.LikeResponse;
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

	private final NaverMapClient naverMapClient;
	private final PlaceMapper placeMapper;
	private final PlaceRepository placeRepository;
	private final GooglePlaceClient googleClient;
	private final PlaceImagesRepository placeImagesRepository;
	private final PresignedUrlService presignedUrlService;
	private final LikedPlacesRepository likedPlacesRepository;
	private final UserRepository userRepository;
	private final ExecutorService executorService;
	private final GeoCoderClient geoCoderClient;

	public List<SavedPlaceInfo> search(double lat, double lng, String keyword) {
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
			.map(response -> CompletableFuture.supplyAsync(() -> convertToSavedPlaceInfo(response, images),
					executorService)
				.completeOnTimeout(fallback(response), 4, TimeUnit.SECONDS)
				.exceptionally(ex -> fallback(response)))
			.toList();

		CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
		return futures.stream().map(CompletableFuture::join).toList();
	}

	@Transactional
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

	private SavedPlaceInfo convertToSavedPlaceInfo(SearchCandidateResponse response, Map<String, List<String>> images) {
		List<String> cached = images.getOrDefault(response.placeKey(), List.of());
		List<String> placeImages = !cached.isEmpty()
			? cached : googleClient.fetchImages(response.name(), response.address(), 3);

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

}
