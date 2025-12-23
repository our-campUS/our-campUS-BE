package com.campus.campus.global.oci.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

public record PresignedUrlResponseDto(
        @Schema(description = "Presigned PUT 업로드 URL")
        String uploadUrl,

        @Schema(description = "업로드 완료 후 접근 가능한 이미지 URL")
        String imageUrl
) {}


