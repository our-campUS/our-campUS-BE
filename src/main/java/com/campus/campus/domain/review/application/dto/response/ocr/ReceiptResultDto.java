package com.campus.campus.domain.review.application.dto.response.ocr;

import java.time.LocalDate;
import java.util.List;

public record ReceiptResultDto(
	String confirmNum,
	String bizNum,
	String storeName,
	String totalPrice,
	LocalDate paymentDate,
	List<ReceiptItemDto> items
) {
}
