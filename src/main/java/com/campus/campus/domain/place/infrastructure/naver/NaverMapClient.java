package com.campus.campus.domain.place.infrastructure.naver;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.campus.campus.domain.place.application.dto.response.naver.NaverSearchResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class NaverMapClient {

	private final NaverMapConfig naverMapConfig;
	private final WebClient naverMapWebClient;

	/**
	 * 사용자가 클릭한 장소를 조회
	 */
	public NaverSearchResponse searchPlaces(String keyword, int count) {
		//네이버 지도 API 연동
		NaverSearchResponse response = naverMapWebClient.get()
			.uri(uriBuilder -> uriBuilder.scheme("https")
				.host("openapi.naver.com")
				.path("/v1/search/local.json")
				.queryParam("query", keyword)
				.queryParam("display", count)
				.build())
			.header("X-Naver-Client-Id", naverMapConfig.clientId)
			.header("X-Naver-Client-Secret", naverMapConfig.clientSecret)
			.retrieve()
			.bodyToMono(NaverSearchResponse.class)
			.block();

		log.info("네이버 검색 응답 = {}", response);
		return response;
	}

}
