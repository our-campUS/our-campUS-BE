package com.campus.campus.domain.manager.presentation;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.campus.campus.domain.inquiry.application.dto.response.InquiryListItemResponse;
import com.campus.campus.domain.manager.application.dto.request.CouncilApproveOrDenyRequest;
import com.campus.campus.domain.manager.application.dto.request.InquirySearchCondition;
import com.campus.campus.domain.manager.application.dto.request.ManagerLoginRequest;
import com.campus.campus.domain.manager.application.dto.request.RewardRequest;
import com.campus.campus.domain.manager.application.dto.response.CertifyRequestCouncilListResponse;
import com.campus.campus.domain.manager.application.dto.response.CertifyRequestCouncilResponse;
import com.campus.campus.domain.manager.application.dto.response.CouncilApproveOrDenyResponse;
import com.campus.campus.domain.manager.application.dto.response.ManagerLoginResponse;
import com.campus.campus.domain.manager.application.dto.response.StampRewardNeededUserListResponse;
import com.campus.campus.domain.manager.application.service.ManagerService;
import com.campus.campus.global.common.response.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/managers")
public class ManagerController {
	private final ManagerService managerService;

	@PostMapping("/login")
	@Operation(summary = "관리자 로그인")
	public CommonResponse<ManagerLoginResponse> login(@Valid @RequestBody ManagerLoginRequest managerLoginRequest) {
		ManagerLoginResponse managerLoginResponse = managerService.login(managerLoginRequest);

		return CommonResponse.success(ManagerResponseCode.MANAGER_LOGIN_SUCCESS, managerLoginResponse);
	}

	@PatchMapping("/approve/council/{councilId}")
	@PreAuthorize("hasRole('MANAGER')")
	@Operation(summary = "학생회 계정 승인")
	public CommonResponse<CouncilApproveOrDenyResponse> approveCouncil(
		@PathVariable Long councilId,
		@Valid @RequestBody CouncilApproveOrDenyRequest councilApproveOrDenyRequest
	) {
		CouncilApproveOrDenyResponse response = managerService.approveOrDenyCouncil(councilId,
			councilApproveOrDenyRequest);

		return CommonResponse.success(ManagerResponseCode.COUNCIL_APPROVE_OR_DENY_SUCCESS, response);
	}

	@GetMapping("/approve/council/{councilId}")
	@PreAuthorize("hasRole('MANAGER')")
	@Operation(summary = "특정 학생회 계정 인증 요청 당선 사진 조회")
	public CommonResponse<CertifyRequestCouncilResponse> getCertifyRequestCouncil(@PathVariable Long councilId) {
		CertifyRequestCouncilResponse response = managerService.getCertifyRequestCouncil(councilId);

		return CommonResponse.success(ManagerResponseCode.CERTIFY_REQUEST_ELECTION_IMAGE_SUCCESS, response);
	}

	@GetMapping("/approve/councils")
	@PreAuthorize("hasRole('MANAGER')")
	@Operation(summary = "학생회 인증 요청 목록 조회")
	public CommonResponse<List<CertifyRequestCouncilListResponse>> getCertifyRequestCouncils() {
		List<CertifyRequestCouncilListResponse> responses = managerService.getCertifyRequestCouncils();

		return CommonResponse.success(ManagerResponseCode.CERTIFY_REQUEST_LIST_SUCCESS, responses);
	}

	@GetMapping("/rewards/necessary-users")
	@PreAuthorize("hasRole('MANAGER')")
	@Operation(summary = "스탬프 보상이 필요한 유저 목록 조회")
	public CommonResponse<List<StampRewardNeededUserListResponse>> getStampRewardNeededUserList() {
		List<StampRewardNeededUserListResponse> responses = managerService.getStampRewardNeededUserList();

		return CommonResponse.success(ManagerResponseCode.REWARD_NEEDED_USER_LIST_SUCCESS, responses);
	}

	@PostMapping("reward/grant/{userId}")
	@PreAuthorize("hasRole('MANAGER')")
	@Operation(summary = "스탬프 보상 지급")
	public CommonResponse<Void> grantRewardToUser(
		@PathVariable Long userId,
		@Valid @RequestBody RewardRequest rewardRequest
	) {
		managerService.grantRewardToUser(userId, rewardRequest);

		return CommonResponse.success(ManagerResponseCode.GRANT_REWARD_SUCCESS);
	}

	@GetMapping("/inquiries")
	@PreAuthorize("hasRole('MANAGER')")
	@Operation(
		summary = "전체 문의 내역 조회",
		description = "작성자 타입과 답변 상태에 따라 문의를 필터링하여 조회합니다."
	)
	public CommonResponse<List<InquiryListItemResponse>> getAllInquiries(
		@ParameterObject @Valid InquirySearchCondition condition
	) {
		List<InquiryListItemResponse> response = managerService.getAllInquiries(condition);
		return CommonResponse.success(ManagerResponseCode.INQUIRY__LIST_SUCCESS, response);
	}
}
