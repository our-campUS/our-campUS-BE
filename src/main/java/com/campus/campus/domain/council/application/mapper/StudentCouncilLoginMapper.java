package com.campus.campus.domain.council.application.mapper;

import org.springframework.stereotype.Component;

import com.campus.campus.domain.council.application.dto.request.StudentCouncilSignUpRequest;
import com.campus.campus.domain.council.application.dto.response.StudentCouncilLoginResponse;
import com.campus.campus.domain.council.domain.entity.StudentCouncil;
import com.campus.campus.domain.school.domain.entity.College;
import com.campus.campus.domain.school.domain.entity.Major;
import com.campus.campus.domain.school.domain.entity.School;
import com.campus.campus.global.config.SecurityConfig;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StudentCouncilLoginMapper {
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
			studentCouncil.getMajor() != null ? studentCouncil.getMajor().getMajorName() : null
		);
	}
}
