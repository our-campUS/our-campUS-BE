package com.campus.campus.domain.review.infrastructure;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.campus.campus.domain.review.application.exception.ReceiptImageFormatException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ClovaOcrClient {

	private final RestClient clovaOcrRestClient;

	@Value("${clova.ocr.invoke-url}")
	private String invokeUrl;

	@Value("${clova.ocr.secret-key}")
	private String secretKey;

	public String requestReceiptOcr(byte[] imageBytes, String originalFilename) {
		//이미지->Base64
		String base64Image = Base64.getEncoder().encodeToString(imageBytes);
		String format = extractFormat(originalFilename);

		//json body 구성
		Map<String, Object> image = new HashMap<>();
		image.put("format", format);
		image.put("data", base64Image);
		image.put("name", "receipt_test2");

		//message 파트
		Map<String, Object> body = new HashMap<>();
		body.put("version", "V2");
		body.put("requestId", UUID.randomUUID().toString());
		body.put("timestamp", System.currentTimeMillis());
		body.put("images", List.of(image));

		return clovaOcrRestClient.post()
			.uri(invokeUrl)
			.header("X-OCR-SECRET", secretKey)
			.contentType(MediaType.APPLICATION_JSON)
			.body(body)
			.retrieve() // 응답 받기 시작
			.body(String.class);
	}

	private String extractFormat(String originalFilename) {
		if (originalFilename == null) {
			throw new ReceiptImageFormatException();
		}

		String lower = originalFilename.toLowerCase();

		if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
			return "jpg";
		}

		if (lower.endsWith(".png")) {
			return "png";
		}

		throw new ReceiptImageFormatException();
	}
}
