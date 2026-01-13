package com.campus.campus.domain.review.application.service;

import java.io.File;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.campus.campus.domain.review.application.dto.response.ocr.ReceiptItemDto;
import com.campus.campus.domain.review.application.dto.response.ocr.ReceiptOcrResponse;
import com.campus.campus.domain.review.application.dto.response.ocr.ReceiptResultDto;
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
		File imageFile = convertToFile(file);

		//ocr
		String rawResponse = clovaOcrClient.requestReceiptOcr(imageFile);

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
		var receipt = response.images().get(0).receipt().result();

		return new ReceiptResultDto(
			receipt.storeInfo().name().text(), //점포 정보
			receipt.paymentInfo().totalPrice().price().text(), //결제(가격) 정보
			receipt.paymentInfo().date().text(), //결제일자 정보
			receipt.subResults().get(0).items().stream()
				.map(i -> new ReceiptItemDto(
					i.name().text(),
					i.price().text()
				))
				.toList()
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
}
