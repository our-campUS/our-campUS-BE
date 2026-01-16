package com.campus.campus.domain.review.application.dto.response.ocr;

import java.time.LocalDate;
import java.util.List;

public record ReceiptResultDto(
	String storeName,
	String totalPlace,
	LocalDate paymentDate,
	List<ReceiptItemDto> items
) {
}