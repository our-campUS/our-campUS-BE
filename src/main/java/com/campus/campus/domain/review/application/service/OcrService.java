package com.campus.campus.domain.review.application.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.campus.campus.domain.place.application.dto.response.SavedPlaceInfo;
import com.campus.campus.domain.place.application.service.PlaceService;
import com.campus.campus.domain.place.domain.entity.Place;
import com.campus.campus.domain.review.application.dto.response.ReviewPartnerResponse;
import com.campus.campus.domain.review.application.dto.response.ocr.PaymentInfo;
import com.campus.campus.domain.review.application.dto.response.ocr.PriceInfo;
import com.campus.campus.domain.review.application.dto.response.ocr.ReceiptItemDto;
import com.campus.campus.domain.review.application.dto.response.ocr.ReceiptOcrResponse;
import com.campus.campus.domain.review.application.dto.response.ocr.ReceiptResultDto;
import com.campus.campus.domain.review.application.dto.response.ocr.ReceiptWrapper;
import com.campus.campus.domain.review.application.dto.response.ocr.StoreInfo;
import com.campus.campus.domain.review.application.dto.response.ocr.TextField;
import com.campus.campus.domain.review.application.dto.response.ocr.TotalPrice;
import com.campus.campus.domain.review.application.exception.ReceiptFileConvertException;
import com.campus.campus.domain.review.application.exception.ReceiptOcrFailedException;
import com.campus.campus.domain.review.infrastructure.ocr.ClovaOcrClient;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class OcrService {

	private final ClovaOcrClient clovaOcrClient;
	private final ObjectMapper objectMapper;
	private final ReviewService reviewService;
	private final PlaceService placeService;

	public ReviewPartnerResponse processReceipt(MultipartFile file, Long userId, SavedPlaceInfo placeInfo) {
		Place place = placeService.findOrCreatePlace(placeInfo);
		Long placeId = place.getPlaceId();

		//MultipartFIle -> byte[]
		byte[] imageBytes;
		try {
			imageBytes = file.getBytes();
		} catch (IOException e) {
			throw new ReceiptFileConvertException();
		}

		//ocr
		String rawResponse = clovaOcrClient.requestReceiptOcr(imageBytes, file.getOriginalFilename());
		log.info("[OCR RAW RESPONSE] {}", rawResponse);

		ReceiptOcrResponse ocrResponse = parse(rawResponse);
		ReceiptResultDto result = extractReceiptResult(ocrResponse);
		log.info("영수증 ocr 인식 결과:{}", result);

		return reviewService.findPartnership(placeId, result, userId);
	}

	private ReceiptOcrResponse parse(String json) {
		try {
			log.debug("[OCR PARSE INPUT] {}", json);
			return objectMapper.readValue(json, ReceiptOcrResponse.class);
		} catch (Exception e) {
			log.error("[OCR PARSE FAILED] raw={}", json, e);
			throw new ReceiptOcrFailedException();
		}
	}

	private ReceiptResultDto extractReceiptResult(ReceiptOcrResponse response) {
		log.info("[OCR RESPONSE] images size={}",
			response.images() != null ? response.images().size() : null);
		//images 존재 검증
		var image = response.images().stream()
			.findFirst()
			.orElseThrow(() -> {
				log.warn("[OCR FAILED] images empty");
				return new ReceiptOcrFailedException();
			});

		var receipt = Optional.ofNullable(image.receipt())
			.map(ReceiptWrapper::result)
			.orElseThrow(() -> {
				log.warn("[OCR FAILED] receipt.result is null");
				return new ReceiptOcrFailedException();
			});

		//상호명
		String storeName = Optional.ofNullable(receipt.storeInfo())
			.map(StoreInfo::name)
			.map(TextField::text)
			.orElseThrow(() -> {
				log.warn("[OCR FAILED] storeName missing");
				return new ReceiptOcrFailedException();
			});

		//총액
		String totalPrice = Optional.ofNullable(receipt.totalPrice())
			.map(TotalPrice::price)
			.map(TextField::text)
			.orElseThrow(() -> {
				log.warn("[OCR FAILED] totalPrice missing, paymentInfo={}",
					receipt.paymentInfo());
				return new ReceiptOcrFailedException();
			});

		//결제일
		LocalDate paymentDate = Optional.ofNullable(receipt.paymentInfo())
			.map(PaymentInfo::date)
			.map(TextField::text)
			.map(text -> LocalDate.parse(text, DateTimeFormatter.BASIC_ISO_DATE))
			.orElse(null);

		//상품 목록
		List<ReceiptItemDto> items = Optional.ofNullable(receipt.subResults())
			.orElse(List.of())
			.stream()
			.flatMap(sr -> Optional.ofNullable(sr.items()).orElse(List.of()).stream())
			.map(i -> new ReceiptItemDto(
				safeText(i.name()),
				Optional.ofNullable(i.price())
					.map(PriceInfo::price)
					.map(TextField::text)
					.orElse(null)
			))

			.toList();
		log.info("[OCR ITEMS] count={}", items.size());

		return new ReceiptResultDto(
			storeName,
			totalPrice,
			paymentDate,
			items
		);
	}

	private String safeText(TextField field) {
		return field != null ? field.text() : null;
	}
}