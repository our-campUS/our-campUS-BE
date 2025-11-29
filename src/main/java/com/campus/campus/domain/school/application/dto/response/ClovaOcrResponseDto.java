package com.campus.campus.domain.school.application.dto.response;

import java.util.List;
import lombok.Data;

@Data
public class ClovaOcrResponseDto {

    private List<ImageResult> images;

    @Data
    public static class ImageResult {
        private List<Field> fields;
    }

    @Data
    public static class Field {
        private String inferText;
    }
}
