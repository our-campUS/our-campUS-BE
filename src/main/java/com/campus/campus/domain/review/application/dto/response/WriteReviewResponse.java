package com.campus.campus.domain.review.application.dto.response;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record WriteReviewResponse(
	@Schema(description = "생성된 리뷰 ID", example = "1")
	Long id,

	@Schema(description = "작성자 ID", example = "100")
	Long userId,

	@Schema(description = "작성자 이름", example = "홍길동")
	String userName,

	@Schema(description = "작성일", example = "2024-02-03")
	LocalDate createDate,

	@Schema(description = "장소 ID", example = "50")
	Long placeId,

	@Schema(description = "리뷰 내용", example = "맛있어요")
	String content,

	@Schema(description = "별점", example = "5.0")
	Double star,

	@Schema(description = "리뷰 이미지 URL", example = "https://example.com/image.jpg")
	String imageUrl
) {
}
