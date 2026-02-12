package com.campus.campus.domain.review.application.dto.response.ocr;

import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public record ReceiptResultDto(
	@Schema(description = "상점명", example = "스타벅스 중앙대점")
	String storeName,

	@Schema(description = "총 결제 금액", example = "15000")
	String totalPrice,

	@Schema(description = "결제 일자", example = "2024-02-03")
	LocalDate paymentDate,

	@Schema(description = "구매 품목 목록")
	List<ReceiptItemDto> items
) {
}
