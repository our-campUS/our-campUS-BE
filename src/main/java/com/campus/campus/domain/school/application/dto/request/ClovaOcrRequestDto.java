package com.campus.campus.domain.school.application.dto.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

public record ClovaOcrRequestDto(
        String version,
        String requestId,
        long timestamp,
        List<Image> images
) {

    public static ClovaOcrRequestDto of(String requestId, long timestamp, List<Image> images) {
        return new ClovaOcrRequestDto(
                "V2",
                requestId,
                timestamp,
                images
        );
    }

    public String toJson() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("OCR 요청 JSON 직렬화 실패", e);
        }
    }

    public static record Image(
            String format,
            String name
    ) {
        public static Image of(String format, String name) {
            return new Image(format, name);
        }
    }
}
