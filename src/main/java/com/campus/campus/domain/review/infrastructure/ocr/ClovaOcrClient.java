package com.campus.campus.domain.review.infrastructure.ocr;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
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

	// public String requestReceiptOcr(File imageFile) {
	// 	MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
	//
	// 	body.add("message", createMessage());
	// 	body.add("file", new FileSystemResource(imageFile));
	//
	// 	HttpHeaders headers = new HttpHeaders();
	// 	headers.setContentType(MediaType.MULTIPART_FORM_DATA);
	// 	headers.set("X-OCR-SECRET", secretKey);
	//
	// 	HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
	//
	// 	ResponseEntity<String> response =
	// 		restTemplate.postForEntity(invokeUrl, request, String.class);
	// 	return response.getBody();
	// }

	private String createMessage() {
		return """
			{
			  "images":[{"format":"jpg","name":"receipt"}],
			  "requestId":"%s",
			  "version":"V2",
			  "timestamp":%d
			}
			""".formatted(UUID.randomUUID(), System.currentTimeMillis());
	}
}
