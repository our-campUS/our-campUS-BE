package com.campus.campus.global.oci.application.dto.request;

import lombok.Data;

@Data
public class PresignedUrlRequestDto {
    private String contentType;

    public String getExtension() {
        return switch (contentType) {
            case "image/png" -> ".png";
            case "image/jpeg" -> ".jpg";
            default -> ".bin";
        };
    }
}
