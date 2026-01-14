package com.campus.campus.domain.place.application.dto.response;

import java.util.List;

import com.campus.campus.domain.place.domain.entity.Coordinate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record SearchPlaceInfoResponse(
	@Schema(description = "해당 장소명", example = "숙명여자대학교")
	@NotBlank
	String placeName,

	@Schema(description = "장소 식별 고유 ID")
	@NotBlank
	String placeKey,

	@Schema(description = "장소 주소", example = "서울특별시 용산구 청파로47길 99")
	@NotBlank
	String address,

	@Schema(description = "장소 카테고리", example = "교육,학문>대학교")
	@NotBlank
	String category,

	@Schema(description = "장소 상세 정보 네이버 페이지 하이퍼링크")
	String link,

	@Schema(description = "전화번호", example = "010-1234-1234")
	String telephone,

	@Schema(description = "위도/경도")
	Coordinate coordinate,

	@Schema(description = "이미지 url")
	List<String> imgUrls,

	@Schema(description = "현재 사용자의 좋아요 여부", accessMode = Schema.AccessMode.READ_ONLY)
	boolean isLiked,

	@Schema(description = "해당 장소와 제휴된 학생회 정보 목록", accessMode = Schema.AccessMode.READ_ONLY)
	List<SearchPartnershipInfoResponse> partnerships,

	@Schema(description = "리뷰 평점 평균 (없으면 0.0)", example = "4.5", accessMode = Schema.AccessMode.READ_ONLY)
	Double averageStar
) {
}
