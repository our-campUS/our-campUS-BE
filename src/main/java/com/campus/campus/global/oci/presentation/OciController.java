package com.campus.campus.global.oci.presentation;

import com.campus.campus.global.common.response.CommonResponse;
import com.campus.campus.global.config.OciConfig;
import com.campus.campus.global.oci.OciPresignedUrlService;
import com.campus.campus.global.oci.application.dto.request.PresignedUrlRequestDto;
import com.campus.campus.global.oci.application.dto.response.PresignedUrlResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/storage")
@Tag(name = "Object Storage", description = "OCI 스토리지 공통 API")
public class OciController {

    private final OciPresignedUrlService presignedUrlService;
    private final OciConfig ociConfig;
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Presigned PUT URL 생성 (FINAL 업로드 전용)
     */
    @PostMapping("/presigned")
    @Operation(summary = "이미지 업로드용 Presigned URL 생성")
    public CommonResponse<PresignedUrlResponseDto> createPresignedUrl(
            @RequestBody PresignedUrlRequestDto request
    ) {
        String objectName = "uploads/" + UUID.randomUUID() + request.getExtension();

        String uploadUrl = presignedUrlService.createPresignedPutUrl(objectName);
        String finalUrl = ociConfig.fullObjectUrl(objectName);

        return CommonResponse.success(
                OciResponseCode.PRESIGNED_URL_SUCCESS,
                new PresignedUrlResponseDto(uploadUrl, finalUrl)
        );
    }

    /**
     * Presigned URL 업로드 테스트 (Swagger 전용)
     * 실제 서비스 로직에서는 사용하지 않음
     */
    @PostMapping(
            value = "/test-upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Operation(summary = "Presigned URL 업로드 테스트 (Swagger 전용)")
    public CommonResponse<String> uploadImageTest(
            @RequestParam String presignedUrl,
            @RequestParam MultipartFile file
    ) throws IOException {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(file.getContentType()));

        HttpEntity<byte[]> requestEntity =
                new HttpEntity<>(file.getBytes(), headers);

        restTemplate.exchange(
                presignedUrl,
                HttpMethod.PUT,
                requestEntity,
                String.class
        );

        return CommonResponse.success(
                OciResponseCode.PRESIGNED_URL_SUCCESS,
                "업로드 성공"
        );
    }
}

