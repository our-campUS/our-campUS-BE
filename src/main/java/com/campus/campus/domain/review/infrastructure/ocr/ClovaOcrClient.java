package com.campus.campus.domain.review.infrastructure.ocr;

import java.io.File;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ClovaOcrClient {

	private final RestTemplate restTemplate;

	@Value("${clova.ocr.invoke-url}")
	private String invokeUrl;

	@Value("${clova.ocr.secret-key}")
	private String secretKey;

	public String requestReceiptOcr(File imageFile) {
		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

		//message 파트
		body.add("message", createMessage());
		body.add("file", new FileSystemResource(imageFile));

		HttpHeaders headers = new HttpHeaders();
		headers.set("X-OCR-SECRET", secretKey);
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

		ResponseEntity<String> response =
			restTemplate.postForEntity(invokeUrl, request, String.class);
		return response.getBody();
	}

	private String createMessage() {
		return """
			{
			  "version":"V2",
			  "requestId":"%s",
			  "timestamp":%d,
			  "images":[{"format":"jpg","name":"file"}],
			}
			""".formatted(UUID.randomUUID(), System.currentTimeMillis());
	}
}
