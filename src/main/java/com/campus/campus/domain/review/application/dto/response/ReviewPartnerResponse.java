package com.campus.campus.domain.review.application.dto.response;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;

public record ReviewPartnerResponse(
	@Schema(description = "장소 이름", example = "스타벅스 중앙대점")
	String placeName,

	@Schema(description = "장소 카테고리", example = "CAFE")
	String placeCategory,

	@Schema(description = "제휴 주체", example = "총학생회")
	String council,

	@Schema(description = "리뷰 별점", example = "4.5")
	double star,

	@Schema(description = "제휴 제목", example = "전 메뉴 10% 할인")
	String title,

	@Schema(description = "태그 (예: 제휴)", example = "제휴")
	String tag,

	@Schema(description = "찜 여부", example = "true")
	boolean isLiked,

	@Schema(description = "결제 일자 (영수증 인증 시)", example = "2024-02-03")
	LocalDate paymentDate
) {
}
