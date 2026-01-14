package com.campus.campus.domain.councilpost.application.dto.request;

import java.time.LocalDateTime;
import java.util.List;

import com.campus.campus.domain.councilpost.domain.entity.PostCategory;
import com.campus.campus.domain.councilpost.domain.entity.ThumbnailIcon;
import com.campus.campus.domain.place.application.dto.response.SavedPlaceInfo;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PostRequest(

	@NotNull
	PostCategory category,

	@NotBlank
	String title,

	@NotBlank
	String content,

	@Schema(example =
		"""
			"placeName": "숙명여자대학교",
			      "placeKey": "string",
			      "address": "서울특별시 용산구 청파로47길 99",
			      "category": "교육,학문>대학교",
			      "link": "https://map.naver.com/v5/search/%EC%88%99%EB%AA%85%EC%97%AC%EC%9E%90%EB%8C%80%ED%95%99%EA%B5%90+%EC%A0%9C1%EC%BA%A0%ED%8D%BC%EC%8A%A4?c=37.545947,126.964578,15,0,0,0,dh",
			      "telephone": "010-1234-1234",
			      "coordinate": {
			        "latitude": 0.1,
			        "longitude": 0.1
			      },
			      "imgUrls": [
			        "string"
			      ]
			""",
		description = "/search API에서 반환된 결과 중 하나를 선택")
	@NotNull
	@Valid
	SavedPlaceInfo place,

	@Schema(description = "상세 장소 (예: 310관 B301호)", example = "310관 B301호") // 추가됨
	String detailedLocation,

	@Schema(example = "2025-04-10T18:00")
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	LocalDateTime startDateTime,

	@Schema(example = "2025-04-30T23:59")
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	LocalDateTime endDateTime,

	// 썸네일 (둘 중 하나는 필수)
	String thumbnailImageUrl,
	ThumbnailIcon thumbnailIcon,

	// 본문 이미지들
	List<String> imageUrls
) {
}
