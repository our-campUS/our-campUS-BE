package com.campus.campus.domain.council.application.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campus.campus.domain.council.application.dto.request.StudentCouncilFindPasswordRequest;
import com.campus.campus.domain.council.application.dto.request.StudentCouncilLoginRequest;
import com.campus.campus.domain.council.application.dto.request.StudentCouncilSignUpRequest;
import com.campus.campus.domain.council.application.dto.request.StudentCouncilWithdrawRequest;
import com.campus.campus.domain.council.application.dto.response.StudentCouncilFindIdResponse;
import com.campus.campus.domain.council.application.dto.response.StudentCouncilLoginResponse;
import com.campus.campus.domain.council.application.exception.CouncilIdAndVerifiedEmailInvalidException;
import com.campus.campus.domain.council.application.exception.CouncilSignupForbiddenException;
import com.campus.campus.domain.council.application.exception.EmailAlreadyExistsException;
import com.campus.campus.domain.council.application.exception.InvalidCouncilScopeException;
import com.campus.campus.domain.council.application.exception.LoginIdAlreadyExistsException;
import com.campus.campus.domain.council.application.exception.PasswordNotCorrectException;
import com.campus.campus.domain.council.application.exception.PrecautionNotAgreeException;
import com.campus.campus.domain.council.application.exception.SignupEmailNotFoundException;
import com.campus.campus.domain.council.application.exception.StudentCouncilNotFoundException;
import com.campus.campus.domain.council.application.mapper.StudentCouncilLoginMapper;
import com.campus.campus.domain.council.application.util.CouncilNameGenerator;
import com.campus.campus.domain.council.domain.entity.StudentCouncil;
import com.campus.campus.domain.council.domain.repository.StudentCouncilRepository;
import com.campus.campus.domain.mail.application.exception.EmailVerificationNotFoundException;
import com.campus.campus.domain.mail.application.exception.InvalidEmailVerificationException;
import com.campus.campus.domain.mail.domain.entity.EmailVerification;
import com.campus.campus.domain.mail.domain.entity.VerificationType;
import com.campus.campus.domain.mail.domain.repository.EmailVerificationRepository;
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
import com.campus.campus.global.config.SecurityConfig;
import com.campus.campus.global.util.jwt.JwtProvider;
import com.campus.campus.global.util.jwt.application.service.RedisTokenService;

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
	private final EmailVerificationRepository emailVerificationRepository;
	private final JwtProvider jwtProvider;
	private final SecurityConfig securityConfig;
	private final PasswordEncoder passwordEncoder;
	private final RedisTokenService redisTokenService;
	private final CouncilNameGenerator councilNameGenerator;

	@Value("${jwt.refresh.expiration-seconds}")
	private long refreshTokenExpirationSeconds;

	@Transactional
	public void signUp(StudentCouncilSignUpRequest studentCouncilSignUpRequest) {
		if (studentCouncilRepository.existsByEmailAndDeletedAtIsNotNull(studentCouncilSignUpRequest.email())) {
			throw new CouncilSignupForbiddenException();
		}
		if (studentCouncilRepository.existsByEmail(studentCouncilSignUpRequest.email())) {
			throw new EmailAlreadyExistsException();
		}

		EmailVerification emailVerification = getVerifiedEmail(
			studentCouncilSignUpRequest.email(), VerificationType.SIGNUP);

		if (studentCouncilRepository.existsByLoginId(studentCouncilSignUpRequest.loginId())) {
			throw new LoginIdAlreadyExistsException();
		}

		School school = schoolRepository.findById(studentCouncilSignUpRequest.schoolId())
			.orElseThrow(SchoolNotFoundException::new);

		CouncilScope scope = validateCouncilScope(studentCouncilSignUpRequest, school);

		StudentCouncil studentCouncil = studentCouncilLoginMapper.createStudentCouncil(
			studentCouncilSignUpRequest, school, scope.college, scope.major);

		String councilName = councilNameGenerator.buildCouncilName(studentCouncil);
		studentCouncil.setCouncilName(councilName);

		studentCouncilRepository.save(studentCouncil);

		emailVerification.use();
	}

	public StudentCouncilLoginResponse login(StudentCouncilLoginRequest studentCouncilLoginRequest) {
		StudentCouncil studentCouncil = studentCouncilRepository
			.findByLoginIdAndManagerApprovedIsTrueAndDeletedAtIsNull(studentCouncilLoginRequest.loginId())
			.orElseThrow(StudentCouncilNotFoundException::new);

		if (!passwordEncoder.matches(studentCouncilLoginRequest.password(), studentCouncil.getPassword())) {
			throw new PasswordNotCorrectException();
		}

		String accessToken = jwtProvider.createCouncilAccessToken(studentCouncil.getId());
		String refreshToken = jwtProvider.createCouncilRefreshToken(studentCouncil.getId());

		redisTokenService.setRefreshToken("COUNCIL", String.valueOf(studentCouncil.getId()), refreshToken,
			refreshTokenExpirationSeconds);

		return studentCouncilLoginMapper.toStudentCouncilLoginResponse(studentCouncil, accessToken, refreshToken);
	}

	@Transactional
	public StudentCouncilFindIdResponse findId(String email) {
		EmailVerification emailVerification = getVerifiedEmail(email, VerificationType.FIND_ID);

		if (!studentCouncilRepository.existsByEmailAndDeletedAtIsNull(email)) {
			throw new SignupEmailNotFoundException();
		}

		StudentCouncil studentCouncil = studentCouncilRepository
			.findByEmailAndManagerApprovedIsTrueAndDeletedAtIsNull(email)
			.orElseThrow(StudentCouncilNotFoundException::new);

		emailVerification.use();

		return studentCouncilLoginMapper.toStudentCouncilFindIdResponse(studentCouncil.getLoginId());
	}

	@Transactional
	public void findPassword(StudentCouncilFindPasswordRequest studentCouncilFindPasswordRequest) {
		StudentCouncil studentCouncil = studentCouncilRepository
			.findByLoginIdAndManagerApprovedIsTrueAndDeletedAtIsNull(studentCouncilFindPasswordRequest.loginId())
			.orElseThrow(StudentCouncilNotFoundException::new);

		if (!studentCouncilFindPasswordRequest.email().equals(studentCouncil.getEmail())) {
			throw new CouncilIdAndVerifiedEmailInvalidException();
		}
		EmailVerification emailVerification = getVerifiedEmail(
			studentCouncilFindPasswordRequest.email(), VerificationType.FIND_PASSWORD);

		String newPassword = securityConfig.passwordEncoder().encode(studentCouncilFindPasswordRequest.password());
		studentCouncil.changePassword(newPassword);

		emailVerification.use();

		studentCouncilRepository.save(studentCouncil);
	}

	@Transactional
	public void withdrawCouncil(Long councilId, StudentCouncilWithdrawRequest studentCouncilWithdrawRequest) {
		StudentCouncil studentCouncil = studentCouncilRepository
			.findByIdAndManagerApprovedIsTrueAndDeletedAtIsNull(councilId)
			.orElseThrow(StudentCouncilNotFoundException::new);

		if (!studentCouncilWithdrawRequest.precaution()) {
			throw new PrecautionNotAgreeException();
		}

		if (!securityConfig.passwordEncoder()
			.matches(studentCouncilWithdrawRequest.password(), studentCouncil.getPassword())) {
			throw new PasswordNotCorrectException();
		}

		studentCouncil.delete(LocalDateTime.now());
		studentCouncilRepository.save(studentCouncil);
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

	private EmailVerification getVerifiedEmail(String email, VerificationType verificationType) {
		EmailVerification emailVerification = emailVerificationRepository
			.findTopByEmailAndVerificationTypeOrderByEmailVerificationIdDesc(email, verificationType)
			.orElseThrow(EmailVerificationNotFoundException::new);

		if (emailVerification.isExpired() || !emailVerification.isVerified() || emailVerification.isUsed()) {
			throw new InvalidEmailVerificationException();
		}

		return emailVerification;
	}

	private record CouncilScope(
		College college,
		Major major
	) {

	}
}
