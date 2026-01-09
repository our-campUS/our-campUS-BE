// package com.campus.campus.domain.place.infrastructure.naver;
//
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Component;
// import org.springframework.web.client.RestClient;
// import org.springframework.web.reactive.function.client.WebClient;
//
// import lombok.extern.slf4j.Slf4j;
//
// @Component
// @Slf4j
// public class NaverDirectionsClient {
//
// 	private final WebClient webClient;
// 	private final String clientId;
// 	private final String clientSecret;
//
// 	public NaverDirectionsClient(
// 		RestClient restClient,
// 		@Value("${map.naver.client-id}") String clientId,
// 		@Value("${map.naver.client-secret}") String clientSecret) {
// 		this.restClient = restClient;
// 		this.clientId = clientId;
// 		this.clientSecret = clientSecret;
// 	}
//
// 	public int getWalkingTimeSeconds(
// 		double startLat, double startLng,
// 		double goalLat, double goalLng
// 	) {
// 		DirectionsResponse response = webClient.get()
// 			.uri(uriBuilder -> uriBuilder
// 				.path("/map-direction/v1/driving")
// 				.queryParam("start", startLng + "," + startLat)
// 				.queryParam("goal", goalLng + "," + goalLat)
// 				.queryParam("option", "pedestrian")
// 				.build()
// 			)
// 			.retrieve()
// 			.bodyToMono(DirectionsResponse.class)
// 			.block();
//
// 		return response.getRoute()
// 			.getTraoptimal()
// 			.get(0)
// 			.getSummary()
// 			.getDuration(); // 초 단위
// 	}
//
// }
