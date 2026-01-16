package com.campus.campus.domain.review.application.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.campus.campus.domain.councilpost.application.exception.PlaceInfoNotFoundException;
import com.campus.campus.domain.place.domain.entity.Place;
import com.campus.campus.domain.place.domain.repository.PlaceRepository;
import com.campus.campus.domain.review.application.dto.response.ReviewPartnerResponse;
import com.campus.campus.domain.review.application.dto.response.ocr.ReceiptItemDto;
import com.campus.campus.domain.review.application.dto.response.ocr.ReceiptOcrResponse;
import com.campus.campus.domain.review.application.dto.response.ocr.ReceiptResultDto;
import com.campus.campus.domain.review.application.exception.DuplicateReceiptException;
import com.campus.campus.domain.review.application.exception.ReceiptDateParseException;
import com.campus.campus.domain.review.application.exception.ReceiptFileConvertException;
import com.campus.campus.domain.review.application.exception.ReceiptOcrFailedException;
import com.campus.campus.domain.review.application.mapper.ReviewMapper;
import com.campus.campus.domain.review.domain.repository.ReviewRepository;
import com.campus.campus.domain.review.infrastructure.ClovaOcrClient;
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
	private final ReviewMapper reviewMapper;
	private final PlaceRepository placeRepository;
	private final ReviewRepository reviewRepository;

	public ReviewPartnerResponse processReceipt(MultipartFile file, Long userId, Long placeId) {
		Place place = placeRepository.findById(placeId)
			.orElseThrow(PlaceInfoNotFoundException::new);

		//MultipartFIle -> byte[]
		byte[] imageBytes;
		try {
			imageBytes = file.getBytes();
		} catch (IOException e) {
			throw new ReceiptFileConvertException();
		}

		//ocr
		String rawResponse = clovaOcrClient.requestReceiptOcr(imageBytes, file.getOriginalFilename());
		log.debug("[OCR RAW RESPONSE] {}", rawResponse);

		ReceiptOcrResponse ocrResponse = parse(rawResponse);
		ReceiptResultDto result = extractReceiptResult(ocrResponse);
		log.info("영수증 ocr 인식 결과:{}", result);

		//영수증 중복 사용 여부 검증
		isDuplicateReceipt(result)
			.filter(Boolean::booleanValue)
			.ifPresent(v -> {
				throw new DuplicateReceiptException();
			});

		return reviewService.findPartnership(place.getPlaceId(), result, userId);
	}

	private Optional<Boolean> isDuplicateReceipt(ReceiptResultDto result) {
		if (result.confirmNum() == null || result.bizNum() == null) {
			return Optional.empty(); // 중복 여부 판단 불가 → 중복 아님으로 처리
		}

		return Optional.of(reviewRepository.existsByConfirmNumberAndBusinessNumber(
			result.confirmNum(), result.bizNum()
		));
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

		var images = Optional.ofNullable(response.images()).orElse(List.of());
		var image = images.stream()
			.findFirst()
			.orElseThrow(() -> {
				log.warn("[OCR FAILED] images empty");
				return new ReceiptOcrFailedException();
			});

		var receipt = Optional.ofNullable(image.receipt())
			.map(ReceiptOcrResponse.ReceiptWrapper::result)
			.orElseThrow(() -> {
				log.warn("[OCR FAILED] receipt.result is null");
				return new ReceiptOcrFailedException();
			});

		//상호명
		String storeName = Optional.ofNullable(receipt.storeInfo())
			.map(ReceiptOcrResponse.StoreInfo::name)
			.map(ReceiptOcrResponse.TextField::text)
			.orElseThrow(() -> {
				log.warn("[OCR FAILED] storeName missing");
				return new ReceiptOcrFailedException();
			});

		//총액
		String totalPrice = Optional.ofNullable(receipt.totalPrice())
			.map(ReceiptOcrResponse.TotalPrice::price)
			.map(ReceiptOcrResponse.TextField::text)
			.orElseThrow(() -> {
				log.warn("[OCR FAILED] totalPrice missing, paymentInfo={}",
					receipt.paymentInfo());
				return new ReceiptOcrFailedException();
			});

		//결제일
		LocalDate paymentDate = Optional.ofNullable(receipt.paymentInfo())
			.map(ReceiptOcrResponse.PaymentInfo::date)
			.map(ReceiptOcrResponse.TextField::text)
			.map(this::parseDate)
			.orElseThrow(() -> {
				log.warn("[OCR FAILED] paymentDate missing");
				return new ReceiptOcrFailedException();
			});

		//상품 목록
		List<ReceiptItemDto> items = Optional.ofNullable(receipt.subResults())
			.orElse(List.of())
			.stream()
			.flatMap(sr -> Optional.ofNullable(sr.items()).orElse(List.of()).stream())
			.map(reviewMapper::toDto)
			.toList();
		log.info("[OCR ITEMS] count={}", items.size());

		//승인 번호
		String confirmNum = Optional.ofNullable(receipt.paymentInfo())
			.map(ReceiptOcrResponse.PaymentInfo::confirmNum)
			.map(ReceiptOcrResponse.ConfirmNum::text)
			.map(ReceiptOcrResponse.TextField::text)
			.orElseThrow(() -> {
				log.warn("[OCR FAILED] confirmNum missing, paymentInfo={}",
					receipt.paymentInfo());
				return new ReceiptOcrFailedException();
			});

		//비즈니스 번호
		String bizNum = Optional.ofNullable(receipt.storeInfo())
			.map(ReceiptOcrResponse.StoreInfo::bizNum)
			.map(ReceiptOcrResponse.TextField::text)
			.orElseThrow(() -> {
				log.warn("[OCR FAILED] confirmNum missing, paymentInfo={}",
					receipt.storeInfo());
				return new ReceiptOcrFailedException();
			});

		return reviewMapper.toReceiptResultDto(confirmNum, bizNum, storeName, totalPrice, paymentDate, items);
	}

	private LocalDate parseDate(String text) {
		if (text == null || text.isBlank())
			return null;
		// 숫자가 아닌 문자 제거
		String normalized = text.replaceAll("[^0-9]", "");
		try {
			return LocalDate.parse(normalized, DateTimeFormatter.BASIC_ISO_DATE);
		} catch (DateTimeParseException e) {
			log.warn("[OCR DATE PARSE FAILED] text={}", text, e);
			throw new ReceiptDateParseException();
		}
	}
}