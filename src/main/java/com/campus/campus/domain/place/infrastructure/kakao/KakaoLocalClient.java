package com.campus.campus.domain.place.infrastructure.kakao;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.campus.campus.domain.place.application.dto.response.kakao.KakaoSearchResponse;
import com.campus.campus.domain.place.application.exception.KakaoMapAPIException;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class KakaoLocalClient {

	private final RestClient restClient;
	private final String apiKey;

	public KakaoLocalClient(
			RestClient restClient,
			@Value("${oauth.kakao.client-id}") String apiKey) {
		this.restClient = restClient;
		this.apiKey = apiKey;
	}

	/**
	 * 좌표 기반 키워드 장소 검색
	 */
	public KakaoSearchResponse searchPlaces(String keyword, double lat, double lng, int radius, int count) {
		KakaoSearchResponse response = restClient.get()
				.uri(uriBuilder -> uriBuilder.scheme("https")
						.host("dapi.kakao.com")
						.path("/v2/local/search/keyword.json")
						.queryParam("query", keyword)
						.queryParam("y", lat)
						.queryParam("x", lng)
						.queryParam("radius", radius)
						.queryParam("size", count)
						.queryParam("sort", "distance")
						.build())
				.header("Authorization", "KakaoAK " + apiKey)
				.retrieve()
				.onStatus(
						status -> status.isError(),
						(request, clientResponse) -> {
							throw new KakaoMapAPIException();
						})
				.body(KakaoSearchResponse.class);

		log.info("카카오 검색 응답 = {}", response);
		return response;
	}

	/**
	 * 키워드만으로 장소 검색
	 */
	public KakaoSearchResponse searchPlaces(String keyword, int count) {
		KakaoSearchResponse response = restClient.get()
				.uri(uriBuilder -> uriBuilder.scheme("https")
						.host("dapi.kakao.com")
						.path("/v2/local/search/keyword.json")
						.queryParam("query", keyword)
						.queryParam("size", count)
						.build())
				.header("Authorization", "KakaoAK " + apiKey)
				.retrieve()
				.onStatus(
						status -> status.isError(),
						(request, clientResponse) -> {
							throw new KakaoMapAPIException();
						})
				.body(KakaoSearchResponse.class);

		log.info("카카오 검색 응답 = {}", response);
		return response;
	}
}
