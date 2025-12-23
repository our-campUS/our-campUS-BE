package com.campus.campus.domain.council.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campus.campus.domain.council.application.dto.request.StudentCouncilChangeEmailRequest;
import com.campus.campus.domain.council.application.exception.EmailAlreadyExistsException;
import com.campus.campus.domain.council.application.exception.StudentCouncilNotFoundException;
import com.campus.campus.domain.council.domain.entity.StudentCouncil;
import com.campus.campus.domain.council.domain.repository.StudentCouncilRepository;
import com.campus.campus.domain.mail.application.exception.EmailNotVerifiedException;
import com.campus.campus.domain.mail.domain.entity.VerificationType;
import com.campus.campus.domain.mail.domain.repository.EmailVerificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouncilService {
	private final StudentCouncilRepository studentCouncilRepository;
	private final EmailVerificationRepository emailVerificationRepository;

	@Transactional
	public void changeEmail(Long councilId, StudentCouncilChangeEmailRequest studentCouncilChangeEmailRequest) {
		StudentCouncil studentCouncil = studentCouncilRepository.findById(councilId)
			.orElseThrow(StudentCouncilNotFoundException::new);

		if (studentCouncilRepository.existsByEmail(studentCouncilChangeEmailRequest.email())) {
			throw new EmailAlreadyExistsException();
		}

		checkVerifiedEmail(studentCouncilChangeEmailRequest.email(), VerificationType.CHANGE_EMAIL);

		studentCouncil.changeEmail(studentCouncilChangeEmailRequest.email());
		studentCouncilRepository.save(studentCouncil);
	}

	private void checkVerifiedEmail(String email, VerificationType verificationType) {
		boolean exists = emailVerificationRepository
			.existsByEmailAndVerificationTypeAndVerifiedIsTrue(email, verificationType);

		if (!exists) {
			throw new EmailNotVerifiedException();
		}
	}
}
