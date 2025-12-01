package com.campus.campus.global.ocr.presentation;

import com.campus.campus.global.common.response.CommonResponse;
import com.campus.campus.global.common.response.OciResponseCode;
import com.campus.campus.global.ocr.service.OciPresignedUrlService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/storage")
@Tag(name = "Object Storage", description = "OCI 스토리지 공통 API")
public class OciController {

    private final OciPresignedUrlService presignedUrlService;

    @PostMapping("/presigned")
    public CommonResponse<String> getPresignedUrl(@RequestParam String fileName) {
        String url = presignedUrlService.createPresignedPutUrl(fileName);
        return CommonResponse.success(OciResponseCode.PRESIGNED_URL_SUCCESS, url);
    }
}

