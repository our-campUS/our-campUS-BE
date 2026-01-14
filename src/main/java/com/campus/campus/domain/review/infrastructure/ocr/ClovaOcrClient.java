package com.campus.campus.domain.review.infrastructure.ocr;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.campus.campus.domain.review.application.exception.ReceiptImageFormatException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ClovaOcrClient {

	private final RestTemplate restTemplate;

	@Value("${clova.ocr.invoke-url}")
	private String invokeUrl;

	@Value("${clova.ocr.secret-key}")
	private String secretKey;

	public String requestReceiptOcr(byte[] imageBytes, String originalFilename) {
		//이미지->Base64
		String base64Image = Base64.getEncoder().encodeToString(imageBytes);
		extractFormat(originalFilename);

		//json body 구성
		Map<String, Object> image = new HashMap<>();
		image.put("format", "png");
		image.put("data", base64Image);
		image.put("name", "receipt_test2");

		//message 파트
		Map<String, Object> body = new HashMap<>();
		body.put("version", "V2");
		body.put("requestId", UUID.randomUUID().toString());
		body.put("timestamp", System.currentTimeMillis());
		body.put("images", List.of(image));

		//header
		HttpHeaders headers = new HttpHeaders();
		headers.set("X-OCR-SECRET", secretKey);
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

		//호출
		ResponseEntity<String> response =
			restTemplate.postForEntity(invokeUrl, request, String.class);
		return response.getBody();
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
