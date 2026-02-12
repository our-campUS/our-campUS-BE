package com.campus.campus.domain.review.application.dto.response.ocr;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public record ReceiptOcrResponse(
	@Schema(description = "이미지 분석 결과 목록")
	List<ImageResult> images
) {

	public record ImageResult(
		@Schema(description = "영수증 데이터 래퍼")
		ReceiptWrapper receipt
	) {
	}

	public record ReceiptWrapper(
		@Schema(description = "영수증 분석 결과")
		ReceiptResult result
	) {
	}

	public record ReceiptResult(
		@Schema(description = "상점 정보")
		StoreInfo storeInfo,

		@Schema(description = "결제 정보")
		PaymentInfo paymentInfo,

		@Schema(description = "총 금액 정보")
		TotalPrice totalPrice,

		@Schema(description = "세부 항목 결과 목록")
		List<SubResult> subResults
	) {
	}

	public record StoreInfo(
		@Schema(description = "상점명")
		TextField name
	) {
	}

	public record PaymentInfo(
		@Schema(description = "결제 일시")
		TextField date,

		@Schema(description = "총 금액")
		TotalPrice totalPrice
	) {
	}

	public record TotalPrice(
		@Schema(description = "금액 텍스트")
		TextField price
	) {
	}

	public record SubResult(
		@Schema(description = "영수증 품목 목록")
		List<ReceiptOcrItem> items
	) {
	}

	public record TextField(
		@Schema(description = "인식된 원본 텍스트", example = "스타벅스")
		String text,

		@Schema(description = "포맷팅된 값 정보")
		Formatted formatted,

		@Schema(description = "신뢰도 점수 (0~1)", example = "0.998")
		Double confidenceScore
	) {
	}

	public record Formatted(
		@Schema(description = "포맷팅된 값 (년월일, 금액 등)", example = "2024-01-01")
		String value
	) {
	}

	public record ReceiptOcrItem(
		@Schema(description = "상품명")
		TextField name,

		@Schema(description = "가격 정보")
		PriceInfo price
	) {
	}

	public record PriceInfo(
		@Schema(description = "총 가격")
		TextField price,

		@Schema(description = "단가")
		TextField unitPrice
	) {
	}
}
