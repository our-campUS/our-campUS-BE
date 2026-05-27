package com.campus.campus.domain.review.application.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record PartnershipReviewRequest(
	@NotNull
	@Schema(description = "리뷰 내용", example = "아주 정말 맛있습니다. 저의 완전 짱 또간집. 꼭꼮꼬꼬꼭 가세요.")
	String content,

	@NotNull
	@DecimalMin(value = "0.0", inclusive = true)
	@DecimalMax(value = "5.0", inclusive = true)
	@Schema(description = "평점", example = "3.5")
	Double star,

	@Schema(description = "ocr 인증 여부", example = "true")
	Boolean isVerified,

	@Schema(description = "리뷰 이미지")
	List<String> imageUrls
) {
}
