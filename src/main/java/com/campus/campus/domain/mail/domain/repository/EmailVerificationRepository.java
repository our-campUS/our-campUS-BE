package com.campus.campus.domain.mail.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.campus.campus.domain.mail.domain.entity.EmailVerification;
import com.campus.campus.domain.mail.domain.entity.VerificationType;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
	Optional<EmailVerification> findTopByEmailOrderByEmailVerificationIdDesc(String email);

	boolean existsByEmailAndVerificationTypeAndVerifiedIsTrue(String email, VerificationType verificationType);

}
