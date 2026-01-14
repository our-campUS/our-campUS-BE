package com.campus.campus.domain.review.application.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.campus.campus.domain.review.application.dto.response.ocr.PaymentInfo;
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

	public ReceiptResultDto processReceipt(MultipartFile file) {
		//MultipartFIle -> byte[]
		byte[] imageBytes;
		try {
			imageBytes = file.getBytes();
		} catch (IOException e) {
			throw new ReceiptFileConvertException();
		}

		//ocr
		String rawResponse = clovaOcrClient.requestReceiptOcr(imageBytes, file.getOriginalFilename());

		ReceiptOcrResponse ocrResponse = parse(rawResponse);
		return extractReceiptResult(ocrResponse);
	}

	private ReceiptOcrResponse parse(String json) {
		try {
			return objectMapper.readValue(json, ReceiptOcrResponse.class);
		} catch (Exception e) {
			throw new ReceiptOcrFailedException();
		}
	}

	private ReceiptResultDto extractReceiptResult(ReceiptOcrResponse response) {
		//images 존재 검증
		var image = response.images().stream()
			.findFirst()
			.orElseThrow(ReceiptOcrFailedException::new);

		var receipt = Optional.ofNullable(image.receipt())
			.map(ReceiptWrapper::result)
			.orElseThrow(ReceiptOcrFailedException::new);

		//상호명
		String storeName = Optional.ofNullable(receipt.storeInfo())
			.map(StoreInfo::name)
			.map(TextField::text)
			.orElseThrow(ReceiptOcrFailedException::new);

		//총액
		String totalPrice = Optional.ofNullable(receipt.paymentInfo())
			.map(PaymentInfo::totalPrice)
			.map(TotalPrice::price)
			.map(TextField::text)
			.orElseThrow(ReceiptOcrFailedException::new);

		//결제일
		String paymentDate = Optional.ofNullable(receipt.paymentInfo())
			.map(PaymentInfo::date)
			.map(TextField::text)
			.orElse(null);

		//상품 목록
		List<ReceiptItemDto> items = Optional.ofNullable(receipt.subResults())
			.orElse(List.of())
			.stream()
			.flatMap(sr -> Optional.ofNullable(sr.items()).orElse(List.of()).stream())
			.map(i -> new ReceiptItemDto(
				safeText(i.name()),
				safeText(i.price())
			))
			.toList();

		return new ReceiptResultDto(
			storeName,
			totalPrice,
			paymentDate,
			items
		);
	}

	private File convertToFile(MultipartFile file) {
		try {
			String extension = Objects.requireNonNull(
				file.getOriginalFilename()
			).substring(
				file.getOriginalFilename().lastIndexOf(".")
			);

			File tempFile = File.createTempFile(
				"receipt-",
				extension
			);

			file.transferTo(tempFile);
			return tempFile;
		} catch (Exception e) {
			throw new ReceiptFileConvertException();
		}
	}

	private String safeText(TextField field) {
		return field != null ? field.text() : null;
	}
}
