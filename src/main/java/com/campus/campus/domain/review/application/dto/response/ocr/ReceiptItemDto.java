package com.campus.campus.domain.review.application.dto.response.ocr;

import io.swagger.v3.oas.annotations.media.Schema;

public record ReceiptItemDto(
	@Schema(description = "상품명", example = "아이스 아메리카노")
	String name,

	@Schema(description = "가격", example = "4500")
	String price
) {
}
