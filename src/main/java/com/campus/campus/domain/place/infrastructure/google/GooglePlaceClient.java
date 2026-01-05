package com.campus.campus.domain.place.infrastructure.google;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.campus.campus.domain.place.application.dto.response.google.GooglePhoto;
import com.campus.campus.domain.place.application.dto.response.google.GooglePlaceDetailResponse;
import com.campus.campus.domain.place.application.dto.response.google.GoogleTextSearchResponse;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class GooglePlaceClient {

	private static final String BASE_URL = "https://maps.googleapis.com/maps/api/place";
	private final WebClient webClient;
	private final String apiKey;

	private final Semaphore googleApiSemaphore = new Semaphore(20);

	public GooglePlaceClient(
		@Value("${map.google.places.api-key}") String apiKey
	) {
		this.apiKey = apiKey;
		this.webClient = WebClient.builder()
			.baseUrl(BASE_URL)
			.build();
	}

	/*
	 * 장소 이름 + 주소를 기준으로 google places에서 이미지 URL 목록을 가져옴
	 */
	public List<String> fetchImages(String name, String address, int limit) {
		try{
			googleApiSemaphore.acquire();
		} catch (InterruptedException e){
			throw new RuntimeException("Google API 대기 중 인터럽트 발생", e);
		}

		try{
			String placeId = findPlaceId(name, address);
			if (placeId == null) {
				return List.of();
			}

			//placee details -> photo reference
			List<String> photoRefs = getPhotoReferences(placeId);
			if (photoRefs.isEmpty()) {
				return List.of();
			}

			//imageURL 생성
			List<String> imageUrls = photoRefs.stream()
				.limit(3)
				.map(this::buildPhotoUrl)
				.toList();

			log.info("imageUrls: {}", imageUrls);
			return imageUrls;
		} finally {
			googleApiSemaphore.release();
		}
	}

	/*
	 * text search API를 이용해 place_id를 찾음
	 */
	private String findPlaceId(String name, String address) {
		String query = name + " " + address;

		GoogleTextSearchResponse response = webClient.get()
			.uri(uriBuilder -> uriBuilder
				.path("/textsearch/json")
				.queryParam("query", query)
				.queryParam("key", apiKey)
				.build())
			.retrieve()
			.bodyToMono(GoogleTextSearchResponse.class)
			.timeout(Duration.ofSeconds(3))
			.block();

		if (response == null) {
			return null;
		}

		String placeId = response.results().get(0).placeId();
		if (placeId == null || placeId.isBlank()) {
			return null;
		}
		return placeId;
	}

	/*
	 * Place Details API를 이용해 photo_reference 목록을 가져옴
	 */
	private List<String> getPhotoReferences(String placeId) {

		GooglePlaceDetailResponse response = webClient.get()
			.uri(uriBuilder -> uriBuilder
				.path("/details/json")
				.queryParam("place_id", placeId)
				.queryParam("fields", "photos")
				.queryParam("key", apiKey)
				.build())
			.retrieve()
			.bodyToMono(GooglePlaceDetailResponse.class)
			.timeout(Duration.ofSeconds(3))
			.block();

		if (response == null
			|| response.result() == null
			|| response.result().photos() == null
			|| response.result().photos().isEmpty()) {
			return List.of();
		}

		return response.result().photos().stream()
			.map(GooglePhoto::photoReference)
			.toList();
	}

	/*
	 * photo_reference를 실제 이미지 요청 URL로 변환
	 */
	private String buildPhotoUrl(String photoRef) {
		return "https://maps.googleapis.com/maps/api/place/photo"
			+ "?maxWidth=800"
			+ "&photo_reference=" + photoRef
			+ "&key=" + apiKey;
	}

	/*
	 * OCI 저장 위해 google 이미지 다운로드
	 */
	public byte[] downloadImage(String imageUrl) {

		try {
			return webClient
				.get()
				.uri(imageUrl)               // Google 이미지 URL
				.retrieve()                  // 응답 수신
				.bodyToMono(byte[].class)    // 이미지 바이너리를 byte[]로 변환
				.timeout(Duration.ofSeconds(10)) //타임아웃 제한
				.block();                    // 동기 방식으로 결과 대기

		} catch (Exception e) {
			log.error("Google image download failed. url={}", imageUrl, e);
			throw new IllegalStateException("Google image download failed");
		}
	}
}
