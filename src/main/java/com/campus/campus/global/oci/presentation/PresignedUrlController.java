package com.campus.campus.global.oci.presentation;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.campus.campus.global.common.response.CommonResponse;
import com.campus.campus.global.oci.application.dto.request.PresignedUrlRequestDto;
import com.campus.campus.global.oci.application.dto.response.PresignedUrlResponseDto;
import com.campus.campus.global.oci.application.service.PresignedUrlService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/storage")
@Tag(name = "Object Storage", description = "OCI 스토리지 공통 API")
public class PresignedUrlController {

	private final PresignedUrlService presignedUrlService;

	@PostMapping("/presigned")
	@Operation(summary = "공통 이미지 업로드용 Presigned URL 생성")
	public CommonResponse<PresignedUrlResponseDto> createPresignedUrl(
		@RequestBody @Valid PresignedUrlRequestDto request
	) {
		return CommonResponse.success(
			PresignedUrlResponseCode.PRESIGNED_URL_SUCCESS,
			presignedUrlService.createPresignedUrl("uploads", request)
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
		return CommonResponse.success(
			PresignedUrlResponseCode.PRESIGNED_URL_SUCCESS,
			presignedUrlService.createPresignedUrl("posts/images", request)
		);
	}

}
