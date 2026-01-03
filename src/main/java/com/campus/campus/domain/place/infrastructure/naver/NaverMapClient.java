package com.campus.campus.domain.place.infrastructure.naver;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.campus.campus.domain.place.application.dto.response.naver.NaverSearchResponse;
import com.campus.campus.domain.place.application.exception.NaverMapAPIException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class NaverMapClient {

	private final RestClient restClient;

	@Value("${map.naver.client-id}")
	private String clientId;

	@Value("${map.naver.client-secret}")
	private String clientSecret;

	/**
	 * 사용자가 클릭한 장소를 조회
	 */
	public NaverSearchResponse searchPlaces(String keyword, int count) {
		//네이버 지도 API 연동
		NaverSearchResponse response = restClient.get()
			.uri(uriBuilder -> uriBuilder.scheme("https")
				.host("openapi.naver.com")
				.path("/v1/search/local.json")
				.queryParam("query", keyword)
				.queryParam("display", count)
				.build())
			.header("X-Naver-Client-Id", clientId)
			.header("X-Naver-Client-Secret", clientSecret)
			.retrieve()
			.onStatus(
				status -> status.isError(),
				(request, clientResponse) -> {
					throw new NaverMapAPIException();
				}
			)
			.body(NaverSearchResponse.class);

		log.info("네이버 검색 응답 = {}", response);
		return response;
	}

}
