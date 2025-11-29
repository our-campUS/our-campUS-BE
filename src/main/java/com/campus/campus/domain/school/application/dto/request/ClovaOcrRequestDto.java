package com.campus.campus.domain.school.application.dto.request;

import java.util.List;
import lombok.Data;

@Data
public class ClovaOcrRequestDto {
    private String version = "V2";
    private String requestId;
    private long timestamp;
    private List<Image> images;

    @Data
    public static class Image {
        private String format;
        private String name;
    }
}
