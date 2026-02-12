package com.campus.campus.domain.place.application.dto.response;

import com.campus.campus.domain.place.application.dto.response.kakao.KakaoSearchResponse;

import io.swagger.v3.oas.annotations.media.Schema;

public record SearchCandidateResponse(
		@Schema(description = "카카오 검색 결과 원본 데이터") KakaoSearchResponse.Document document,

		@Schema(description = "장소 이름", example = "스타벅스 중앙대점") String name,

		@Schema(description = "주소", example = "서울특별시 동작구 흑석로 84") String address,

		@Schema(description = "Google Place ID", example = "ChIJN1t_tDeuEmsRUsoyG83frY4") String placeKey,

		@Schema(description = "카카오맵 상세 페이지 URL", example = "https://place.map.kakao.com/12345") String placeUrl) {
}
