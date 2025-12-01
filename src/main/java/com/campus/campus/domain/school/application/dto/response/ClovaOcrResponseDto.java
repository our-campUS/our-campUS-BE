package com.campus.campus.domain.school.application.dto.response;

import java.util.List;

public record ClovaOcrResponseDto(
        String version,
        String requestId,
        long timestamp,
        List<Image> images
) {
    public record Image(
            String uid,
            String name,
            List<Field> fields
    ) {}

    public record Field(
            String inferText,
            Double inferConfidence
    ) {}
}
