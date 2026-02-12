package com.campus.campus.domain.place.application.dto.response.kakao;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

public record KakaoSearchResponse(
	@Schema(description = "검색 결과 문서 목록")
	List<Document> documents
) {

	public record Document(
		@Schema(description = "장소명", example = "스타벅스 강남점")
		@JsonProperty("place_name")
		String placeName,

		@Schema(description = "카테고리", example = "음식점 > 카페")
		@JsonProperty("category_name")
		String categoryName,

		@Schema(description = "지번 주소", example = "서울특별시 강남구 역삼동 123")
		@JsonProperty("address_name")
		String addressName,

		@Schema(description = "도로명 주소", example = "서울특별시 강남구 테헤란로 123")
		@JsonProperty("road_address_name")
		String roadAddressName,

		@Schema(description = "카카오맵 상세 페이지 URL", example = "https://place.map.kakao.com/12345")
		@JsonProperty("place_url")
		String placeUrl,

		@Schema(description = "전화번호", example = "02-1234-5678")
		String phone,

		@Schema(description = "경도 (longitude)", example = "127.0276")
		String x,

		@Schema(description = "위도 (latitude)", example = "37.4979")
		String y,

		@Schema(description = "중심 좌표까지의 거리 (미터)", example = "150")
		String distance
	) {
	}
}
