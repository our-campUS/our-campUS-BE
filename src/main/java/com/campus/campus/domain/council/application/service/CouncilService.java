package com.campus.campus.domain.council.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campus.campus.domain.council.application.dto.request.StudentCouncilChangeEmailRequest;
import com.campus.campus.domain.council.application.dto.request.StudentCouncilChangePasswordRequest;
import com.campus.campus.domain.council.application.dto.request.StudentCouncilChangeProfileImageRequest;
import com.campus.campus.domain.council.application.dto.request.StudentCouncilNicknameRequest;
import com.campus.campus.domain.council.application.dto.response.StudentCouncilChangeProfileImageResponse;
import com.campus.campus.domain.council.application.dto.response.StudentCouncilNicknameResponse;
import com.campus.campus.domain.council.application.dto.response.StudentCouncilProfileResponse;
import com.campus.campus.domain.council.application.exception.EmailAlreadyExistsException;
import com.campus.campus.domain.council.application.exception.NewPasswordConfirmNotMatchException;
import com.campus.campus.domain.council.application.exception.NewPasswordIsCurrentPasswordException;
import com.campus.campus.domain.council.application.exception.PasswordNotCorrectException;
import com.campus.campus.domain.council.application.exception.StudentCouncilNotFoundException;
import com.campus.campus.domain.council.application.mapper.StudentCouncilMapper;
import com.campus.campus.domain.council.domain.entity.StudentCouncil;
import com.campus.campus.domain.council.domain.repository.StudentCouncilRepository;
import com.campus.campus.domain.mail.application.exception.EmailVerificationNotFoundException;
import com.campus.campus.domain.mail.application.exception.InvalidEmailVerificationException;
import com.campus.campus.domain.mail.domain.entity.EmailVerification;
import com.campus.campus.domain.mail.domain.entity.VerificationType;
import com.campus.campus.domain.mail.domain.repository.EmailVerificationRepository;
import com.campus.campus.global.config.SecurityConfig;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouncilService {
	private final StudentCouncilRepository studentCouncilRepository;
	private final EmailVerificationRepository emailVerificationRepository;
	private final SecurityConfig securityConfig;
	private final StudentCouncilMapper studentCouncilMapper;

	@Transactional
	public void changeEmail(Long councilId, StudentCouncilChangeEmailRequest studentCouncilChangeEmailRequest) {
		StudentCouncil studentCouncil = studentCouncilRepository
			.findByIdAndManagerApprovedIsTrueAndDeletedAtIsNull(councilId)
			.orElseThrow(StudentCouncilNotFoundException::new);

		//soft delete된 유저가 예상치 못하게 계정을 복구해야할 수도 있기에 이를 막기 위해 deleteAt이 존재하더라도 조회되게 한다.
		if (studentCouncilRepository.existsByEmail(studentCouncilChangeEmailRequest.email())) {
			throw new EmailAlreadyExistsException();
		}

		EmailVerification emailVerification = getVerifiedChangeEmail(councilId,
			studentCouncilChangeEmailRequest.email());

		studentCouncil.changeEmail(studentCouncilChangeEmailRequest.email());
		emailVerification.use();
		studentCouncilRepository.save(studentCouncil);
	}

	@Transactional
	public void changePassword(Long councilId,
		StudentCouncilChangePasswordRequest studentCouncilChangePasswordRequest) {
		StudentCouncil studentCouncil = studentCouncilRepository
			.findByIdAndManagerApprovedIsTrueAndDeletedAtIsNull(councilId)
			.orElseThrow(StudentCouncilNotFoundException::new);

		if (!securityConfig.passwordEncoder()
			.matches(studentCouncilChangePasswordRequest.currentPassword(), studentCouncil.getPassword())) {
			throw new PasswordNotCorrectException();
		}

		if (studentCouncilChangePasswordRequest.newPassword()
			.equals(studentCouncilChangePasswordRequest.currentPassword())) {
			throw new NewPasswordIsCurrentPasswordException();
		}

		if (!studentCouncilChangePasswordRequest.newPassword()
			.equals(studentCouncilChangePasswordRequest.newPasswordConfirm())) {
			throw new NewPasswordConfirmNotMatchException();
		}

		String newPassword = securityConfig.passwordEncoder().encode(studentCouncilChangePasswordRequest.newPassword());
		studentCouncil.changePassword(newPassword);

		studentCouncilRepository.save(studentCouncil);
	}

	@Transactional
	public StudentCouncilNicknameResponse changeCouncilNickname(Long councilId,
		StudentCouncilNicknameRequest studentCouncilNicknameRequest) {
		StudentCouncil studentCouncil = studentCouncilRepository
			.findByIdAndManagerApprovedIsTrueAndDeletedAtIsNull(councilId)
			.orElseThrow(StudentCouncilNotFoundException::new);

		studentCouncil.updateCouncilNickname(studentCouncilNicknameRequest.councilNickname());
		studentCouncilRepository.save(studentCouncil);

		return studentCouncilMapper.toStudentCouncilNicknameResponse(studentCouncil);
	}

	@Transactional
	public StudentCouncilChangeProfileImageResponse changeCouncilProfileImage(Long councilId,
		StudentCouncilChangeProfileImageRequest studentCouncilChangeProfileImageRequest) {
		StudentCouncil studentCouncil = studentCouncilRepository
			.findByIdAndManagerApprovedIsTrueAndDeletedAtIsNull(councilId)
			.orElseThrow(StudentCouncilNotFoundException::new);

		studentCouncil.updateCouncilProfileImage(studentCouncilChangeProfileImageRequest.councilProfileImageUrl());
		studentCouncilRepository.save(studentCouncil);

		return studentCouncilMapper.toStudentCouncilChangeProfileImageResponse(studentCouncil);
	}

	private EmailVerification getVerifiedChangeEmail(Long councilId, String email) {
		EmailVerification emailVerification = emailVerificationRepository
			.findTopByEmailAndVerificationTypeAndCouncilIdOrderByEmailVerificationIdDesc(email,
				VerificationType.CHANGE_EMAIL, councilId)
			.orElseThrow(EmailVerificationNotFoundException::new);

		if (emailVerification.isExpired() || !emailVerification.isVerified() || emailVerification.isUsed()) {
			throw new InvalidEmailVerificationException();
		}

		return emailVerification;
	}

	public StudentCouncilProfileResponse getCouncilProfile(Long councilId) {
		StudentCouncil council = studentCouncilRepository.findByIdAndDeletedAtIsNull(councilId)
			.orElseThrow(StudentCouncilNotFoundException::new);

		return StudentCouncilMapper.toStudentCouncilProfileResponse(council);
	}
}
