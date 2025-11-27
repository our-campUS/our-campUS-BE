package com.campus.campus.domain.user.presentation;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.campus.campus.domain.user.application.dto.request.UserProfileRequest;
import com.campus.campus.domain.user.application.dto.response.UserFirstProfileResponse;
import com.campus.campus.domain.user.application.service.UserService;
import com.campus.campus.global.auth.annotation.CurrentUserId;
import com.campus.campus.global.common.response.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
	private final UserService userService;

	@PatchMapping("/profile")
	@Operation(summary = "최초 로그인 사용자 학적정보 입력")
	public CommonResponse<UserFirstProfileResponse> createUserProfile(@CurrentUserId Long userId,
		@RequestBody @Valid UserProfileRequest userProfileRequest) {
		UserFirstProfileResponse userFirstProfileResponse = userService.writeUserProfile(userId, userProfileRequest);

		return CommonResponse.success(UserResponseCode.FIRST_PROFILE_WRITE, userFirstProfileResponse);
	}
}
