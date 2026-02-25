package com.campus.campus.domain.place.application.dto.response;

import java.util.List;

import com.campus.campus.domain.place.domain.entity.Coordinate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SavedPlaceInfo(
	@Schema(description = "DB에 저장된 장소 ID (미저장 시 null)", example = "10")
	Long placeId,

	@Schema(description = "해당 장소명", example = "서울여자대학교")
	@NotBlank
	String placeName,

	@Schema(description = "장소 식별 고유 ID", example = "123456789")
	@NotBlank
	String placeKey,

	@Schema(description = "장소 주소(없을 수 있음)", example = "서울특별시 노원구 화랑로 621")
	String address,

	@Schema(description = "장소 카테고리 (category_group_name, 없으면 null)", example = "카페")
	String category,

	@Schema(description = "장소 상세 링크(카카오 place_url 등)", example = "https://place.map.kakao.com/123456789")
	String link,

	@Schema(description = "전화번호", example = "010-1234-1234")
	String telephone,

	@Schema(description = "위도/경도")
	@NotNull
	Coordinate coordinate,

	@Schema(description = "이미지 url")
	List<String> imgUrls
) {}