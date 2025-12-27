package com.campus.campus.domain.manager.presentation;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.campus.campus.domain.manager.application.dto.request.ManagerLoginRequest;
import com.campus.campus.domain.manager.application.dto.response.ManagerLoginResponse;
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
}
