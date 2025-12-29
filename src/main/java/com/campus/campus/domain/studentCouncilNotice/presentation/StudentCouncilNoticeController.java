package com.campus.campus.domain.studentCouncilNotice.presentation;

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

import com.campus.campus.domain.studentCouncilNotice.application.dto.request.NoticeRequestDto;
import com.campus.campus.domain.studentCouncilNotice.application.dto.response.NoticeListItemResponseDto;
import com.campus.campus.domain.studentCouncilNotice.application.dto.response.NoticeResponseDto;
import com.campus.campus.domain.studentCouncilNotice.application.service.StudentCouncilNoticeService;
import com.campus.campus.global.annotation.CurrentUserId;
import com.campus.campus.global.common.response.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
	public CommonResponse<NoticeResponseDto> create(
		@CurrentUserId Long councilId,
		@RequestBody NoticeRequestDto dto
	) {
		NoticeResponseDto response = noticeService.create(councilId, dto);

		return CommonResponse.success(NoticeResponseCode.NOTICE_CREATE_SUCCESS, response);
	}

	@GetMapping("/{noticeId}")
	@Operation(summary = "학생회 공지 단건 조회")
	public CommonResponse<NoticeResponseDto> getNotice(
		@PathVariable Long noticeId,
		@CurrentUserId(required = false) Long currentUserId
	) {
		NoticeResponseDto response = noticeService.findById(noticeId, currentUserId);

		return CommonResponse.success(NoticeResponseCode.NOTICE_READ_SUCCESS, response);
	}

	@GetMapping
	@Operation(summary = "학생회 공지 목록 조회")
	public CommonResponse<Page<NoticeListItemResponseDto>> getNotices(
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "10") int size,
		@CurrentUserId(required = false) Long currentUserId
	) {
		Page<NoticeListItemResponseDto> response = noticeService.findAll(page, size, currentUserId);

		return CommonResponse.success(NoticeResponseCode.NOTICE_LIST_READ_SUCCESS, response);
	}

	@PutMapping("/{noticeId}")
	@PreAuthorize("hasRole('COUNCIL')")
	@Operation(summary = "학생회 공지 수정")
	public CommonResponse<NoticeResponseDto> update(
		@PathVariable Long noticeId,
		@CurrentUserId Long councilId,
		@RequestBody NoticeRequestDto dto
	) {
		NoticeResponseDto response = noticeService.update(councilId, noticeId, dto);

		return CommonResponse.success(NoticeResponseCode.NOTICE_UPDATE_SUCCESS, response);
	}

	@DeleteMapping("/{noticeId}")
	@PreAuthorize("hasRole('COUNCIL')")
	@Operation(summary = "학생회 공지 삭제")
	public CommonResponse<Void> delete(
		@PathVariable Long noticeId,
		@CurrentUserId Long councilId
	) {
		noticeService.delete(councilId, noticeId);

		return CommonResponse.success(NoticeResponseCode.NOTICE_DELETE_SUCCESS);
	}
}

