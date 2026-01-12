package com.campus.campus.domain.review.application.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class OcrService {

	// private final ClovaOcrClient clovaOcrClient;
	//
	// public ReceiptResultDto processReceipt(MultipartFile file) {
	// 	File imageFile = convertToFile(file);
	//
	// 	//ocr
	// 	String rawResponse = clovaOcrClient.requestReceiptOcr(imageFile);
	//
	// 	ReceiptOcrResponse ocrResponse = parse(rawResponse);
	// 	return extractReceiptResult(ocrResponse);
	// }
	//
	// private ReceiptOcrResponse parse(String json) {
	// 	try {
	// 		return objectMapper.readValue(json, ReceiptOcrResponse.class);
	// 	} catch (Exception e) {
	// 		throw new ReceiptOcrFailedException();
	// 	}
	// }
}
