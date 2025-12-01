package com.campus.campus.domain.school.application.service.ocr;

import com.campus.campus.domain.school.application.dto.request.ClovaOcrRequestDto;
import com.campus.campus.domain.school.application.dto.response.ClovaOcrResponseDto;
import com.campus.campus.domain.school.application.exception.SchoolOcrCallFailedException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;


@Service
@RequiredArgsConstructor
public class ClovaOcrCallerService {

    @Value("${clova.ocr.invoke-url}")
    private String invokeUrl;

    @Value("${clova.ocr.secret-key}")
    private String secretKey;

    public ClovaOcrResponseDto callOcr(byte[] fileBytes, String imageName) {
        try {

            String ext = extractExtension(imageName);

            ClovaOcrRequestDto request = ClovaOcrRequestDto.of(
                    UUID.randomUUID().toString(),
                    System.currentTimeMillis(),
                    List.of(ClovaOcrRequestDto.Image.of(ext, imageName))
            );



            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            builder.part("message", request.toJson())
                    .contentType(MediaType.APPLICATION_JSON);

            builder.part("file", fileBytes)
                    .filename(imageName)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM);

            WebClient client = WebClient.builder()
                    .baseUrl(invokeUrl)
                    .defaultHeader("X-OCR-SECRET", secretKey)
                    .build();

            return client.post()
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(builder.build()))
                    .retrieve()
                    .bodyToMono(ClovaOcrResponseDto.class)
                    .block();

        } catch (Exception e) {
            throw new SchoolOcrCallFailedException();
        }
    }
    private String extractExtension(String filename) {
        if (filename == null) return "jpg";

        int idx = filename.lastIndexOf(".");
        return (idx == -1)
                ? "jpg"
                : filename.substring(idx + 1);
    }
}

