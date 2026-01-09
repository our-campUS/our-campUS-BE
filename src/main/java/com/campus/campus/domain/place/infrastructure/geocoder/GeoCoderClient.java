package com.campus.campus.domain.place.infrastructure.geocoder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.campus.campus.domain.place.application.dto.response.geocoder.AddressResponse;
import com.campus.campus.domain.place.application.exception.GeoCoderException;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class GeoCoderClient {
	private static final String BASE_URL = "https://api.vworld.kr";
	private final WebClient webClient;
	private final String apiKey;

	public GeoCoderClient(
		@Value("${map.geocoder.api-key}") String apiKey
	) {
		this.apiKey = apiKey;
		this.webClient = WebClient.builder().baseUrl(BASE_URL).build();
	}

	/*
	 * 현재 위치 위/경도 -> 주소
	 */
	public AddressResponse getAddress(double lat, double lng) {
		AddressResponse response = webClient.get()
			.uri(uriBuilder -> uriBuilder
				.path("/req/address")
				.queryParam("service", "address")
				.queryParam("request", "getAddress")
				.queryParam("key", apiKey)
				.queryParam("point", lng + "," + lat)
				.queryParam("crs", "epsg:4326")
				.queryParam("type", "both")
				.queryParam("format", "json")
				.build())
			.retrieve()
			.onStatus(
				status -> status.isError(),
				res -> Mono.error(new GeoCoderException())
			)
			.bodyToMono(AddressResponse.class).block();

		return response;
	}
}
