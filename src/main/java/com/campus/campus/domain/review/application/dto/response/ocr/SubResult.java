package com.campus.campus.domain.review.application.dto.response.ocr;

import java.util.List;

public record SubResult(
	List<ReceiptOcrItem> items
) {
}