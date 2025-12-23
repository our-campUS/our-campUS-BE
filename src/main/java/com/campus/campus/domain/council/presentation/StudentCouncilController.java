package com.campus.campus.domain.council.presentation;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.campus.campus.domain.council.application.dto.request.StudentCouncilChangeEmailRequest;
import com.campus.campus.domain.council.application.service.CouncilService;
import com.campus.campus.global.annotation.CurrentUserId;
import com.campus.campus.global.common.response.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('COUNCIL')")
@RequestMapping("/council")
public class StudentCouncilController {
	private final CouncilService councilService;

	@PostMapping("/change/email")
	@Operation(summary = "학생회 이메일 변경")
	CommonResponse<Void> changeEmail(@CurrentUserId Long councilId,
		@Valid @RequestBody StudentCouncilChangeEmailRequest studentCouncilChangeEmailRequest) {
		councilService.changeEmail(councilId, studentCouncilChangeEmailRequest);

		return CommonResponse.success(StudentCouncilResponseCode.CHANGE_EMAIL_SUCCESS);
	}
}
