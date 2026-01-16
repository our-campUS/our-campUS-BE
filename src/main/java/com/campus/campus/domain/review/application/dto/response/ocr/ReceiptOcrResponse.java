package com.campus.campus.domain.review.application.dto.response.ocr;

import java.util.List;

public record ReceiptOcrResponse(
	List<ImageResult> images
) {

	public record ImageResult(
		ReceiptWrapper receipt
	) {
	}

	public record ReceiptWrapper(
		ReceiptResult result
	) {
	}

	public record ReceiptResult(
		StoreInfo storeInfo,
		PaymentInfo paymentInfo,
		TotalPrice totalPrice,
		List<SubResult> subResults
	) {
	}

	public record StoreInfo(
		TextField name
	) {
	}

	public record PaymentInfo(
		TextField date,
		TotalPrice totalPrice,
		ConfirmNum confirmNum //승인 번호
	) {
	}

	public record ConfirmNum(
		TextField text
	) {
	}

	public record TotalPrice(
		TextField price
	) {
	}

	public record SubResult(
		List<ReceiptOcrItem> items
	) {
	}

	public record TextField(
		String text,
		Formatted formatted,
		Double confidenceScore
	) {
	}

	public record Formatted(
		String value
	) {
	}

	public record ReceiptOcrItem(
		TextField name,
		PriceInfo price
	) {
	}

	public record PriceInfo(
		TextField price,
		TextField unitPrice
	) {
	}
}
