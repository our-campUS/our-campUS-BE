package com.campus.campus.domain.review.application.dto.response.ocr;

import java.util.List;

public record ReceiptResult(
	StoreInfo storeInfo,
	PaymentInfo paymentInfo,
	TotalPrice totalPrice,
	List<SubResult> subResults
) {
}
