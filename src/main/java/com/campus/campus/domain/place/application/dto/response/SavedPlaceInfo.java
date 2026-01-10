package com.campus.campus.domain.place.application.dto.response;

import java.util.List;

import com.campus.campus.domain.place.domain.entity.Coordinate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record SavedPlaceInfo(

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

	@Schema(description = "장소 상세 정보 네이버 페이지 하이퍼링크", example = "https://map.naver.com/v5/search/%EC%88%99%EB%AA%85%EC%97%AC%EC%9E%90%EB%8C%80%ED%95%99%EA%B5%90+%EC%A0%9C1%EC%BA%A0%ED%8D%BC%EC%8A%A4?c=37.545947,126.964578,15,0,0,0,dh")
	String link,

	@Schema(description = "전화번호", example = "010-1234-1234")
	String telephone,

	@Schema(description = "위도/경도")
	@NotBlank
	Coordinate coordinate,

	@Schema(description = "이미지 url")
	List<String> imgUrls

) {
}
