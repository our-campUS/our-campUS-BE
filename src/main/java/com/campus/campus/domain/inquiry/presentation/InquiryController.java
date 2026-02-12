package com.campus.campus.domain.inquiry.presentation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.campus.campus.domain.inquiry.application.dto.request.InquiryCreateRequest;
import com.campus.campus.domain.inquiry.application.dto.response.InquiryCreateResponse;
import com.campus.campus.domain.inquiry.application.dto.response.InquiryListItemResponse;
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

	@GetMapping("/me")
	@Operation(
		summary = "내 문의 내역 조회",
		description = "내가 작성한 문의 목록을 최신순으로 페이징하여 조회합니다. 아코디언 UI를 위해 상세 내용과 답변을 포함합니다."
	)
	public CommonResponse<Page<InquiryListItemResponse>> getMyInquiries(
		@CurrentUserId Long userId,
		@PageableDefault(size = 10) Pageable pageable
	) {
		Page<InquiryListItemResponse> response = inquiryService.getMyInquiries(userId, pageable);
		return CommonResponse.success(InquiryResponseCode.INQUIRY_READ_SUCCESS, response);
	}
}
