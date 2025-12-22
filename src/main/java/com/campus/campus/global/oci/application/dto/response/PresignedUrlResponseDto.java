package com.campus.campus.global.oci.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PresignedUrlResponseDto {
    private String uploadUrl;
    private String tempUrl;
}
