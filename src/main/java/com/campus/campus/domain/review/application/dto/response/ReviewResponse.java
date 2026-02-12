package com.campus.campus.domain.review.application.dto.response;

import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record ReviewResponse(
	@Schema(description = "리뷰 ID", example = "1")
	Long id,

	@Schema(description = "작성자 유저 ID", example = "100")
	Long userId,

	@Schema(description = "작성자 이름", example = "홍길동")
	String userName,

	@Schema(description = "작성일", example = "2024-02-03")
	LocalDate createDate,

	@Schema(description = "장소 ID", example = "50")
	Long placeId,

	@Schema(description = "리뷰 내용", example = "커피가 맛있고 분위기가 좋아요.")
	String content,

	@Schema(description = "별점", example = "4.5")
	Double star,

	@Schema(description = "리뷰 이미지 URL 목록")
	List<String> imageUrls
) {
}
