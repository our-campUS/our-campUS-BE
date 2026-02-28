package com.campus.campus.domain.place.application.dto.response;

import com.campus.campus.domain.place.domain.entity.Coordinate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NonPartnerPlaceLikeRequest(

	@Schema(description = "장소명", example = "스타벅스 강남점")
	@NotBlank
	String placeName,

	@Schema(description = "장소 주소", example = "서울특별시 강남구 테헤란로 123")
	String address,

	@Schema(description = "카카오 POI/장소 ID(있으면 보내고, 없으면 null 가능)", example = "123456789")
	String placeKey,

	@Schema(description = "위도/경도")
	@NotNull
	Coordinate coordinate

) {
}
