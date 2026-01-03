package com.campus.campus.domain.councilnotice.presentation;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.campus.campus.domain.councilnotice.application.dto.request.NoticeRequest;
import com.campus.campus.domain.councilnotice.application.dto.response.NoticeListItemResponse;
import com.campus.campus.domain.councilnotice.application.dto.response.NoticeResponse;
import com.campus.campus.domain.councilnotice.application.service.StudentCouncilNoticeService;
import com.campus.campus.global.annotation.CurrentCouncilId;
import com.campus.campus.global.common.response.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/student-council/notices")
@Tag(name = "Student Council Notice", description = "학생회(COUNCIL) 권한 전용 공지 게시글 관리 API")
public class StudentCouncilNoticeController {

	private final StudentCouncilNoticeService noticeService;

	@PostMapping
	@PreAuthorize("hasRole('COUNCIL')")
	@Operation(summary = "학생회 공지 작성")
	public CommonResponse<NoticeResponse> create(
		@CurrentCouncilId Long councilId,
		@RequestBody @Valid NoticeRequest dto
	) {
		NoticeResponse response = noticeService.create(councilId, dto);

		return CommonResponse.success(NoticeResponseCode.NOTICE_CREATE_SUCCESS, response);
	}

	@GetMapping
	@Operation(summary = "학생회 공지 목록 조회")
	public CommonResponse<Page<NoticeListItemResponse>> getNotices(
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "10") int size,
		@CurrentCouncilId(required = false) Long councilId
	) {
		Page<NoticeListItemResponse> response = noticeService.findAll(page, size, councilId);

		return CommonResponse.success(NoticeResponseCode.NOTICE_LIST_READ_SUCCESS, response);
	}

	@PutMapping("/{noticeId}")
	@PreAuthorize("hasRole('COUNCIL')")
	@Operation(summary = "학생회 공지 수정")
	public CommonResponse<NoticeResponse> update(
		@PathVariable Long noticeId,
		@CurrentCouncilId Long councilId,
		@RequestBody @Valid NoticeRequest dto
	) {
		NoticeResponse response = noticeService.update(councilId, noticeId, dto);

		return CommonResponse.success(NoticeResponseCode.NOTICE_UPDATE_SUCCESS, response);
	}

	@DeleteMapping("/{noticeId}")
	@PreAuthorize("hasRole('COUNCIL')")
	@Operation(summary = "학생회 공지 삭제")
	public CommonResponse<Void> delete(
		@PathVariable Long noticeId,
		@CurrentCouncilId Long councilId
	) {
		noticeService.delete(councilId, noticeId);

		return CommonResponse.success(NoticeResponseCode.NOTICE_DELETE_SUCCESS);
	}

	@GetMapping("/{noticeId}")
	@Operation(summary = "학생회 공지 단건 조회")
	public CommonResponse<NoticeResponse> getNotice(
		@PathVariable Long noticeId,
		@CurrentCouncilId(required = false) Long councilId
	) {
		NoticeResponse response = noticeService.findById(noticeId, councilId);

		return CommonResponse.success(NoticeResponseCode.NOTICE_READ_SUCCESS, response);
	}
}
