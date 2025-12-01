package com.campus.campus.domain.storage.application.presentation;

import com.campus.campus.domain.school.domain.entity.OcrImageType;
import com.campus.campus.domain.school.domain.entity.OcrStatus;
import com.campus.campus.domain.school.domain.entity.SchoolOcrImage;
import com.campus.campus.domain.school.domain.repository.SchoolOcrImageRepository;
import com.campus.campus.domain.storage.application.dto.PresignedUrlResponse;
import com.campus.campus.global.auth.annotation.CurrentUserId;
import com.campus.campus.global.common.response.CommonResponse;
import com.campus.campus.global.common.response.OciResponseCode;
import com.campus.campus.global.ocr.service.OciPresignedUrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/storage")
@Tag(name = "Object Storage", description = "OCI Object Storage 업로드용 API")
public class StorageController {

    private final OciPresignedUrlService presignedUrlService;
    private final SchoolOcrImageRepository schoolOcrImageRepository;

    @Operation(summary = "이미지 업로드용 Presigned URL 발급",
            description = "학생증 / 학생회 당선증 이미지를 업로드하기 위한 Presigned URL을 발급합니다.")
    @PostMapping("/presigned-url")
    public CommonResponse<PresignedUrlResponse> createPresignedUrl(
            @CurrentUserId Long userId,
            @RequestParam OcrImageType type
    ) {
        String objectName = userId + "_" + UUID.randomUUID() + "_" + type.name() + ".jpg";
        String uploadUrl = presignedUrlService.createPresignedPutUrl(objectName);

        SchoolOcrImage image = SchoolOcrImage.builder()
                .userId(userId)
                .objectName(objectName)
                .type(type)
                .status(OcrStatus.REQUESTED)
                .build();

        SchoolOcrImage saved = schoolOcrImageRepository.save(image);

        return CommonResponse.success(
                OciResponseCode.PRESIGNED_URL_SUCCESS,
                new PresignedUrlResponse(saved.getId(), objectName, uploadUrl)
        );
    }
}
