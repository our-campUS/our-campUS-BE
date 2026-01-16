package com.campus.campus.domain.review.application.dto.request;

import java.util.List;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PartnershipReviewRequest(
	@NotNull
	@Size(min = 10, message = "리뷰 내용은 최소 10자 이상이어야 합니다.")
	@Schema(example = "아주 정말 맛있습니다. 저의 완전 짱 또간집. 꼭꼮꼬꼬꼭 가세요.")
	String content,

	@NotNull
	@DecimalMin(value = "0.0", inclusive = true)
	@DecimalMax(value = "5.0", inclusive = true)
	@Schema(example = "3.5")
	Double star,

	@Schema(description = "영수증 리뷰를 하고 오면 isVerified=True로 주세요.")
	Boolean isVerified,

	List<String> imageUrls,

	// 승인 번호 (OCR 추론값, 존재하지 않을 수 있음)
	@Nullable
	@Schema(
		description = "영수증 OCR 결과에서 추론한 승인 번호입니다. OCR 인식 결과에 따라 값이 없을 수 있습니다.",
		example = "873492"
	)
	String confirmNum
) {
}
