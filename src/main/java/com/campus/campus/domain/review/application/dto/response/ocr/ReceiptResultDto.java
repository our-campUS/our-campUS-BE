package com.campus.campus.domain.review.application.dto.response.ocr;

import java.util.List;

public record ReceiptResultDto(
	String storeName,
	String totalPlace,
	List<ReceiptItemDto> items
) {
}
