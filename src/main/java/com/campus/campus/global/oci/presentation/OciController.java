package com.campus.campus.global.oci.presentation;

import com.campus.campus.global.common.response.CommonResponse;
import com.campus.campus.global.config.OciConfig;
import com.campus.campus.global.oci.OciPresignedUrlService;
import com.campus.campus.global.oci.application.dto.request.PresignedUrlRequestDto;
import com.campus.campus.global.oci.application.dto.response.PresignedUrlResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/storage")
@Tag(name = "Object Storage", description = "OCI 스토리지 공통 API")
public class OciController {

    private final OciPresignedUrlService presignedUrlService;
    private final OciConfig ociConfig;
    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/presigned")
    @Operation(summary = "공통 이미지 업로드용 Presigned URL 생성")
    public CommonResponse<PresignedUrlResponseDto> createPresignedUrl(
            @RequestBody @Valid PresignedUrlRequestDto request
    ) {
        String objectName = "uploads/" + UUID.randomUUID() + request.resolveExtension();

        String uploadUrl = presignedUrlService.createPresignedPutUrl(objectName);
        String finalUrl = ociConfig.fullObjectUrl(objectName);

        return CommonResponse.success(
                OciResponseCode.PRESIGNED_URL_SUCCESS,
                new PresignedUrlResponseDto(uploadUrl, finalUrl)
        );
    }

    @PostMapping("/posts/images/presigned")
    @Operation(
            summary = "게시글 이미지 업로드용 Presigned URL 생성",
            description = "게시글 본문/썸네일 이미지 업로드 전용 Presigned URL을 생성"
    )
    public CommonResponse<PresignedUrlResponseDto> createPostImagePresignedUrl(
            @RequestBody @Valid PresignedUrlRequestDto request
    ) {
        String objectName =
                "posts/images/" + UUID.randomUUID() + request.resolveExtension();

        String uploadUrl = presignedUrlService.createPresignedPutUrl(objectName);
        String imageUrl = ociConfig.fullObjectUrl(objectName);

        return CommonResponse.success(
                OciResponseCode.PRESIGNED_URL_SUCCESS,
                new PresignedUrlResponseDto(uploadUrl, imageUrl)
        );
    }

}

