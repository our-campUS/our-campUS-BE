package com.campus.campus.domain.inquiry.presentation;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.campus.campus.domain.inquiry.application.dto.request.InquiryCreateRequest;
import com.campus.campus.domain.inquiry.application.dto.response.InquiryCreateResponse;
import com.campus.campus.domain.inquiry.application.service.InquiryService;
import com.campus.campus.global.annotation.CurrentUserId;
import com.campus.campus.global.common.response.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/inquiries")
@RequiredArgsConstructor
public class InquiryController {

	private final InquiryService inquiryService;

	@PostMapping
	@Operation(
		summary = "1:1 문의 등록",
		description = "제목과 내용을 입력하여 새로운 문의를 등록합니다."
	)
	public CommonResponse<InquiryCreateResponse> create(
		@Valid @RequestBody InquiryCreateRequest request,
		@CurrentUserId Long userId
	) {
		InquiryCreateResponse response = inquiryService.createInquiry(userId, request);
		return CommonResponse.success(InquiryResponseCode.INQUIRY_CREATE_SUCCESS, response);
	}
}
