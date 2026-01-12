package com.campus.campus.domain.review.application.dto.request;

import java.util.List;

import com.campus.campus.domain.place.application.dto.response.SavedPlaceInfo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReviewRequest(

	@NotNull
	@Size(min = 20, message = "리뷰 내용은 최소 20자 이상이어야 합니다.")
	@Schema(example = "아주 정말 맛있습니다. 저의 완전 짱 또간집. 꼭꼮꼬꼬꼭 가세요.")
	String content,

	@NotNull
	@Schema(example = "3.5")
	Double star,

	List<String> imageUrls,

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
	SavedPlaceInfo place

) {
}
