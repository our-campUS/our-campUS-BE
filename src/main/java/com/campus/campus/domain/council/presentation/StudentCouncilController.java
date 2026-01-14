package com.campus.campus.domain.council.presentation;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.campus.campus.domain.council.application.dto.request.StudentCouncilChangeEmailRequest;
import com.campus.campus.domain.council.application.dto.request.StudentCouncilChangePasswordRequest;
import com.campus.campus.domain.council.application.dto.request.StudentCouncilNicknameRequest;
import com.campus.campus.domain.council.application.dto.response.StudentCouncilNicknameResponse;
import com.campus.campus.domain.council.application.service.CouncilService;
import com.campus.campus.global.annotation.CurrentCouncilId;
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

	@PatchMapping("/change/email")
	@Operation(summary = "학생회 이메일 변경")
	public CommonResponse<Void> changeEmail(@CurrentCouncilId Long councilId,
		@Valid @RequestBody StudentCouncilChangeEmailRequest studentCouncilChangeEmailRequest) {
		councilService.changeEmail(councilId, studentCouncilChangeEmailRequest);

		return CommonResponse.success(StudentCouncilResponseCode.CHANGE_EMAIL_SUCCESS);
	}

	@PatchMapping("/change/password")
	@Operation(summary = "학생회 비밀번호 변경")
	public CommonResponse<Void> changePassword(@CurrentCouncilId Long councilId,
		@Valid @RequestBody StudentCouncilChangePasswordRequest studentCouncilChangePasswordRequest) {
		councilService.changePassword(councilId, studentCouncilChangePasswordRequest);

		return CommonResponse.success(StudentCouncilResponseCode.CHANGE_PASSWORD_SUCCESS);
	}

	@PatchMapping("/change/nickname")
	@Operation(summary = "학생회 닉네임 변경")
	public CommonResponse<StudentCouncilNicknameResponse> changeNickname(@CurrentCouncilId Long councilId,
		@Valid @RequestBody StudentCouncilNicknameRequest studentCouncilNicknameRequest) {
		StudentCouncilNicknameResponse response = councilService.changeCouncilNickname(councilId,
			studentCouncilNicknameRequest);

		return CommonResponse.success(StudentCouncilResponseCode.CHANGE_NICKNAME_SUCCESS, response);
	}
}
