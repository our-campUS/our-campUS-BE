package com.campus.campus.domain.council.application.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campus.campus.domain.council.application.dto.request.StudentCouncilLoginRequest;
import com.campus.campus.domain.council.application.dto.request.StudentCouncilSignUpRequest;
import com.campus.campus.domain.council.application.dto.response.StudentCouncilLoginResponse;
import com.campus.campus.domain.council.application.exception.InvalidCouncilScopeException;
import com.campus.campus.domain.council.application.exception.LoginIdAlreadyExistsException;
import com.campus.campus.domain.council.application.exception.PasswordNotCollectException;
import com.campus.campus.domain.council.application.exception.StudentCouncilNotFoundException;
import com.campus.campus.domain.council.application.mapper.StudentCouncilLoginMapper;
import com.campus.campus.domain.council.domain.entity.StudentCouncil;
import com.campus.campus.domain.council.domain.repository.StudentCouncilRepository;
import com.campus.campus.domain.school.application.exception.CollegeNotFoundException;
import com.campus.campus.domain.school.application.exception.MajorNotFoundException;
import com.campus.campus.domain.school.application.exception.SchoolCollegeNotSameException;
import com.campus.campus.domain.school.application.exception.SchoolNotFoundException;
import com.campus.campus.domain.school.domain.entity.College;
import com.campus.campus.domain.school.domain.entity.Major;
import com.campus.campus.domain.school.domain.entity.School;
import com.campus.campus.domain.school.domain.repository.CollegeRepository;
import com.campus.campus.domain.school.domain.repository.MajorRepository;
import com.campus.campus.domain.school.domain.repository.SchoolRepository;
import com.campus.campus.global.util.jwt.JwtProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouncilLoginService {
	private final StudentCouncilRepository studentCouncilRepository;
	private final SchoolRepository schoolRepository;
	private final CollegeRepository collegeRepository;
	private final MajorRepository majorRepository;
	private final StudentCouncilLoginMapper studentCouncilLoginMapper;
	private final JwtProvider jwtProvider;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public StudentCouncilLoginResponse signUp(StudentCouncilSignUpRequest studentCouncilSignUpRequest) {
		if (studentCouncilRepository.existsByLoginId(studentCouncilSignUpRequest.loginId())) {
			throw new LoginIdAlreadyExistsException();
		}

		School school = schoolRepository.findById(studentCouncilSignUpRequest.schoolId())
			.orElseThrow(SchoolNotFoundException::new);

		CouncilScope scope = validateCouncilScope(studentCouncilSignUpRequest, school);

		StudentCouncil studentCouncil = studentCouncilLoginMapper.createStudentCouncil(
			studentCouncilSignUpRequest, school, scope.college, scope.major);

		studentCouncilRepository.save(studentCouncil);

		String accessToken = jwtProvider.createCouncilAccessToken(studentCouncil.getId());
		String refreshToken = jwtProvider.createCouncilRefreshToken(studentCouncil.getId());

		return studentCouncilLoginMapper.toStudentCouncilLoginResponse(studentCouncil, accessToken, refreshToken);
	}

	public StudentCouncilLoginResponse login(StudentCouncilLoginRequest studentCouncilLoginRequest) {
		StudentCouncil studentCouncil = studentCouncilRepository.findByLoginId(studentCouncilLoginRequest.loginId())
			.orElseThrow(StudentCouncilNotFoundException::new);

		if (!passwordEncoder.matches(studentCouncilLoginRequest.password(), studentCouncil.getPassword())) {
			throw new PasswordNotCollectException();
		}

		String accessToken = jwtProvider.createCouncilAccessToken(studentCouncil.getId());
		String refreshToken = jwtProvider.createCouncilRefreshToken(studentCouncil.getId());

		return studentCouncilLoginMapper.toStudentCouncilLoginResponse(studentCouncil, accessToken, refreshToken);
	}

	private record CouncilScope(
		College college,
		Major major
	) {

	}

	private CouncilScope validateCouncilScope(
		StudentCouncilSignUpRequest studentCouncilSignUpRequest,
		School school
	) {
		return switch (studentCouncilSignUpRequest.councilType()) {
			case SCHOOL_COUNCIL -> {
				if (studentCouncilSignUpRequest.collegeId() != null || studentCouncilSignUpRequest.majorId() != null) {
					throw new InvalidCouncilScopeException();
				}

				yield new CouncilScope(null, null);
			}

			case COLLEGE_COUNCIL -> {
				if (studentCouncilSignUpRequest.collegeId() == null || studentCouncilSignUpRequest.majorId() != null) {
					throw new InvalidCouncilScopeException();
				}

				College college = collegeRepository.findById(studentCouncilSignUpRequest.collegeId())
					.orElseThrow(CollegeNotFoundException::new);

				if (!college.getSchool().getSchoolId().equals(school.getSchoolId())) {
					throw new SchoolCollegeNotSameException();
				}

				yield new CouncilScope(college, null);
			}

			case MAJOR_COUNCIL -> {
				if (studentCouncilSignUpRequest.majorId() == null) {
					throw new InvalidCouncilScopeException();
				}

				College college = collegeRepository.findById(studentCouncilSignUpRequest.collegeId())
					.orElseThrow(CollegeNotFoundException::new);

				Major major = majorRepository.findById(studentCouncilSignUpRequest.majorId())
					.orElseThrow(MajorNotFoundException::new);

				if (!major.getSchool().getSchoolId().equals(school.getSchoolId())) {
					throw new SchoolCollegeNotSameException();
				}

				yield new CouncilScope(college, major);
			}
		};
	}
}
