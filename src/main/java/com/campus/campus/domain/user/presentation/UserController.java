package com.campus.campus.domain.user.presentation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.campus.campus.domain.user.application.dto.request.CampusNicknameUpdateRequest;
import com.campus.campus.domain.user.application.dto.request.ChangeProfileImageRequest;
import com.campus.campus.domain.user.application.dto.request.ChangeUserAcademicRequest;
import com.campus.campus.domain.user.application.dto.request.UserProfileRequest;
import com.campus.campus.domain.user.application.dto.response.ChangeProfileImageResponse;
import com.campus.campus.domain.user.application.dto.response.ChangeUserAcademicResponse;
import com.campus.campus.domain.user.application.dto.response.UserFirstProfileResponse;
import com.campus.campus.domain.user.application.dto.response.UserInfoIdsResponse;
import com.campus.campus.domain.user.application.dto.response.UserInfoResponse;
import com.campus.campus.domain.user.application.service.UserService;
import com.campus.campus.global.annotation.CurrentUserId;
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

	@PatchMapping("/change/nickname")
	@Operation(summary = "사용자 서비스 내 닉네임 변경")
	public CommonResponse<Void> updateCampusNickname(@CurrentUserId Long userId,
		@Valid @RequestBody CampusNicknameUpdateRequest nicknameUpdateRequest) {
		userService.updateCampusNickname(userId, nicknameUpdateRequest);

		return CommonResponse.success(UserResponseCode.NICKNAME_UPDATE_SUCCESS);
	}


	@PatchMapping("/change/profile/image")
	@Operation(summary = "사용자 프로필 이미지 변경")
	public CommonResponse<ChangeProfileImageResponse> updateProfileImage(@CurrentUserId Long userId,
		@RequestBody @Valid ChangeProfileImageRequest changeProfileImageRequest) {
		ChangeProfileImageResponse response = userService.updateProfileImage(userId, changeProfileImageRequest);

		return CommonResponse.success(UserResponseCode.PROFILE_IMAGE_UPDATE_SUCCESS, response);
	}

	@PatchMapping("change/profile/academic")
	@Operation(summary = "사용자 학적 정보 수정 (3개월 1회 제한)")
	public CommonResponse<ChangeUserAcademicResponse> updateAcademicInfo(@CurrentUserId Long userId,
		@RequestBody @Valid ChangeUserAcademicRequest changeUserAcademicRequest) {
		ChangeUserAcademicResponse response = userService.updateUserAcademic(userId, changeUserAcademicRequest);

		return CommonResponse.success(UserResponseCode.ACADEMIC_INFO_UPDATE_SUCCESS, response);
	}

	@GetMapping
	@Operation(summary = "사용자 정보 조회(홈 화면)")
	public CommonResponse<UserInfoResponse> getUserInfo(@CurrentUserId Long userId) {
		UserInfoResponse userInfoResponse = userService.getUserInfo(userId);

		return CommonResponse.success(UserResponseCode.GET_USER_INFO_SUCCESS, userInfoResponse);
	}

	@GetMapping("/ids")
	@Operation(summary = "사용자 소속 정보 조회(Topic 구독) - ID만")
	public CommonResponse<UserInfoIdsResponse> getUserInfoIdsInfo(@CurrentUserId Long userId) {
		UserInfoIdsResponse res = userService.getUserInfoIds(userId);
		return CommonResponse.success(UserResponseCode.GET_USER_INFO_SUCCESS, res);
	}

}
