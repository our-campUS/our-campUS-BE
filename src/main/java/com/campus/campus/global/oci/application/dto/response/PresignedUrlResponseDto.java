package com.campus.campus.global.oci.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PresignedUrlResponseDto {

    //OCI에 PUT 업로드할 Presigned URL
    private String uploadUrl;

    //업로드 완료 후 최종 이미지 URL
    private String imageUrl;
}

