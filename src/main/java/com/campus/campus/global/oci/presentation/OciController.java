package com.campus.campus.global.oci.presentation;

import com.campus.campus.global.common.response.CommonResponse;
import com.campus.campus.global.config.OciConfig;
import com.campus.campus.global.oci.OciPresignedUrlService;
import com.campus.campus.global.oci.application.dto.request.PresignedUrlRequestDto;
import com.campus.campus.global.oci.application.dto.response.PresignedUrlResponseDto;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.requests.HeadObjectRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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


    @PostMapping("/presigned")
    public CommonResponse<PresignedUrlResponseDto> createGeneralPresignedUrl(
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

    @PostMapping("/temp/presigned")
    public CommonResponse<PresignedUrlResponseDto> createTempImagePresignedUrl(
            @RequestBody PresignedUrlRequestDto request
    ) {
        String objectName = "temp/" + UUID.randomUUID() + request.getExtension();

        String uploadUrl = presignedUrlService.createPresignedPutUrl(objectName);
        String tempUrl = ociConfig.fullObjectUrl(objectName);

        return CommonResponse.success(
                OciResponseCode.PRESIGNED_URL_SUCCESS,
                new PresignedUrlResponseDto(uploadUrl, tempUrl)
        );
    }
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
            headers.set("Content-Type", file.getContentType());

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
                    "업로드 성공: " + presignedUrl
            );


    }

}
