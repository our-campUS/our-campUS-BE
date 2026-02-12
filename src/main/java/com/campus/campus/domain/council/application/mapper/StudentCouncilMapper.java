package com.campus.campus.domain.council.application.mapper;

import org.springframework.stereotype.Component;

import com.campus.campus.domain.council.application.dto.request.StudentCouncilSignUpRequest;
import com.campus.campus.domain.council.application.dto.response.StudentCouncilChangeProfileImageResponse;
import com.campus.campus.domain.council.application.dto.response.StudentCouncilFindIdResponse;
import com.campus.campus.domain.council.application.dto.response.StudentCouncilLoginResponse;
import com.campus.campus.domain.council.application.dto.response.StudentCouncilNicknameResponse;
import com.campus.campus.domain.council.application.dto.response.StudentCouncilProfileResponse;
import com.campus.campus.domain.council.domain.entity.StudentCouncil;
import com.campus.campus.domain.school.domain.entity.College;
import com.campus.campus.domain.school.domain.entity.Major;
import com.campus.campus.domain.school.domain.entity.School;
import com.campus.campus.global.config.SecurityConfig;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StudentCouncilMapper {
	private final SecurityConfig securityConfig;

	public StudentCouncil createStudentCouncil(StudentCouncilSignUpRequest studentCouncilSignUpRequest, School school,
		College college, Major major) {
		String encodedPassword = securityConfig.passwordEncoder().encode(studentCouncilSignUpRequest.password());

		return StudentCouncil.builder()
			.loginId(studentCouncilSignUpRequest.loginId())
			.password(encodedPassword)
			.email(studentCouncilSignUpRequest.email())
			.councilType(studentCouncilSignUpRequest.councilType())
			.school(school)
			.college(college)
			.major(major)
			.electionImageUrl(studentCouncilSignUpRequest.electionImageUrl())
			.managerApproved(false)
			.build();
	}

	public StudentCouncilLoginResponse toStudentCouncilLoginResponse(StudentCouncil studentCouncil, String accessToken,
		String refreshToken) {
		return new StudentCouncilLoginResponse(
			accessToken,
			refreshToken,
			studentCouncil.getId(),
			studentCouncil.getLoginId(),
			studentCouncil.getEmail(),
			studentCouncil.getCouncilType(),
			studentCouncil.getSchool().getSchoolName(),
			studentCouncil.getCollege() != null ? studentCouncil.getCollege().getCollegeName() : null,
			studentCouncil.getMajor() != null ? studentCouncil.getMajor().getMajorName() : null,
			studentCouncil.getCouncilName(),
			studentCouncil.getCouncilNickname(),
			studentCouncil.getCouncilProfileImageUrl(),
			studentCouncil.getCouncilPresident()
		);
	}

	public StudentCouncilFindIdResponse toStudentCouncilFindIdResponse(String loginId) {
		return new StudentCouncilFindIdResponse(
			loginId
		);
	}

	public StudentCouncilNicknameResponse toStudentCouncilNicknameResponse(StudentCouncil studentCouncil) {
		return new StudentCouncilNicknameResponse(
			studentCouncil.getCouncilNickname(),
			studentCouncil.getCouncilName()
		);
	}

	public StudentCouncilChangeProfileImageResponse toStudentCouncilChangeProfileImageResponse(
		StudentCouncil studentCouncil) {
		return new StudentCouncilChangeProfileImageResponse(
			studentCouncil.getId(),
			studentCouncil.getCouncilName(),
			studentCouncil.getCouncilProfileImageUrl()
		);
	}

	public static StudentCouncilProfileResponse toStudentCouncilProfileResponse(StudentCouncil council) {
		return new StudentCouncilProfileResponse(
			council.getId(),
			council.getCouncilNickname(),
			council.getCouncilProfileImageUrl()
		);
	}
}
