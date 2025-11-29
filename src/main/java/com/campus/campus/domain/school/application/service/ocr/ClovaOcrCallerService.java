package com.campus.campus.domain.school.application.service.ocr;

import com.campus.campus.domain.school.application.dto.request.ClovaOcrRequestDto;
import com.campus.campus.domain.school.application.dto.response.ClovaOcrResponseDto;
import com.campus.campus.domain.school.application.exception.SchoolOcrCallFailedException;
import com.campus.campus.global.properties.ClovaOcrProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;


@Service
@RequiredArgsConstructor
public class ClovaOcrCallerService {

    private final ClovaOcrProperties properties;
    private final ObjectMapper mapper = new ObjectMapper();

    private static final String HEADER_SECRET = "X-OCR-SECRET";
    private static final String PART_MESSAGE = "message";
    private static final String PART_FILE = "file";
    private static final String DEFAULT_EXTENSION = "jpg";

    public ClovaOcrResponseDto callOcr(MultipartFile file, String imageName) {

        try {
            // ----- 1. OCR 요청 메시지 생성 -----
            ClovaOcrRequestDto request = new ClovaOcrRequestDto();
            request.setRequestId(UUID.randomUUID().toString());
            request.setTimestamp(System.currentTimeMillis());

            ClovaOcrRequestDto.Image img = new ClovaOcrRequestDto.Image();
            img.setFormat(extractExtension(file.getOriginalFilename()));
            img.setName(imageName);

            request.setImages(List.of(img));

            String jsonMessage = mapper.writeValueAsString(request);

            // ----- 2. multipart body 구성 -----
            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            builder.part(PART_MESSAGE, jsonMessage)
                    .contentType(MediaType.APPLICATION_JSON);

            builder.part(PART_FILE, file.getBytes())
                    .filename(file.getOriginalFilename())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM);

            // ----- 3. WebClient 호출 -----
            WebClient client = WebClient.builder()
                    .baseUrl(properties.getInvokeUrl())
                    .defaultHeader(HEADER_SECRET, properties.getSecretKey())
                    .build();

            return client.post()
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .bodyValue(builder.build())
                    .retrieve()
                    .bodyToMono(ClovaOcrResponseDto.class)
                    .block();

        } catch (IOException e) {
            throw new SchoolOcrCallFailedException();
        }
    }

    private String extractExtension(String filename) {
        if (filename == null) return DEFAULT_EXTENSION;

        int idx = filename.lastIndexOf('.');
        return (idx == -1)
                ? DEFAULT_EXTENSION
                : filename.substring(idx + 1);
    }
}