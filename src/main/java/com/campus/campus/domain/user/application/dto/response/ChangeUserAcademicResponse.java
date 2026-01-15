package com.campus.campus.domain.user.application.dto.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

public record ChangeUserAcademicResponse(
	@Schema(description = "유저 id", example = "1")
	Long userId,

	@Schema(description = "유저 닉네임(campusNickname 설정했으면 campusNickname, 아니면, nickname")
	String nickname,

	@Schema(description = "변경된 학교 이름", example = "가천대학교")
	String schoolName,

	@Schema(description = "변경된 단과대 이름", example = "IT융합대학")
	String collegeName,

	@Schema(description = "변경된 학과 이름", example = "소프트웨어학과")
	String majorName,

	@Schema(description = "다음 변경 가능 날짜", example = "2026-07-15T12:00:00")
	LocalDateTime nextUpdateAvailableDate
) {
}