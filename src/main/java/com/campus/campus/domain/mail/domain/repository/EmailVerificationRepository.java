package com.campus.campus.domain.mail.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.campus.campus.domain.mail.domain.entity.EmailVerification;
import com.campus.campus.domain.mail.domain.entity.VerificationType;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
	Optional<EmailVerification> findTopByEmailAndVerificationTypeOrderByEmailVerificationIdDesc(
		String email, VerificationType verificationType);

	Optional<EmailVerification> findTopByEmailAndVerificationTypeAndCouncilIdOrderByEmailVerificationIdDesc(
		String email, VerificationType verificationType, Long councilId);
}
