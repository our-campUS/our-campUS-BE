package com.campus.campus.domain.council.application.dto.response;

import com.campus.campus.domain.council.domain.entity.CouncilType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record StudentCouncilLoginResponse(
	@Schema(description = "access token", example = "랜덤 accessToken")
	String accessToken,

	@Schema(description = "refresh token", example = "랜덤 refreshToken")
	String refreshToken,

	@Schema(description = "학생회 ID", example = "1")
	Long councilId,

	@Schema(description = "로그인 id", example = "dede1234")
	String loginId,

	@Schema(description = "인증 이메일", example = "campus@campus.com")
	String email,

	@Schema(description = "학생회 타입", example = "SCHOOL_COUNCIL")
	CouncilType councilType,

	@Schema(description = "학교 이름", example = "가천대학교")
	String schoolName,

	@Schema(description = "단과대 이름 (없으면 null)", example = "IT융합대학")
	String collegeName,

	@Schema(description = "학과 이름 (없으면 null)", example = "컴퓨터공학과")
	String majorName,

	@Schema(description = "학생회 이름", example = "가천대학교 총학생회")
	String councilName,

	@Schema(description = "학생회 닉네임", example = "CUBE")
	String councilNickname,

	@Schema(description = "학생회 프로필 이미지 url", example = "https://www.example.com.png")
	String councilProfileImageUrl,

	@Schema(description = "학생회 대표자 이름", example = "한승현")
	String councilPresident
) {
}
