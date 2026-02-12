package com.campus.campus.domain.review.application.dto.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record SimpleReviewResponse(
	@Schema(description = "별점", example = "4.5")
	double star,

	@Schema(description = "작성자 이름", example = "홍길동")
	String writerName,

	@Schema(description = "리뷰 내용", example = "커피가 맛있어요")
	String content,

	@Schema(description = "리뷰 썸네일 이미지 URL", example = "https://example.com/image.jpg")
	String thumbnailImgUrl,

	@Schema(description = "작성 일시")
	LocalDateTime createdAt
) {
}
